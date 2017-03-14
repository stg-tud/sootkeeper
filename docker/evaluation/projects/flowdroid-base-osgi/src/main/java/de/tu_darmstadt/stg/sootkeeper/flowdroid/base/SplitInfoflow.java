package de.tu_darmstadt.stg.sootkeeper.flowdroid.base;


import heros.solver.CountingThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.InfoflowConfiguration;
import soot.jimple.infoflow.InfoflowManager;
import soot.jimple.infoflow.aliasing.FlowSensitiveAliasStrategy;
import soot.jimple.infoflow.aliasing.IAliasingStrategy;
import soot.jimple.infoflow.aliasing.PtsBasedAliasStrategy;
import soot.jimple.infoflow.cfg.BiDirICFGFactory;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.AbstractionAtSink;
import soot.jimple.infoflow.data.AccessPathFactory;
import soot.jimple.infoflow.data.FlowDroidMemoryManager;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.data.pathBuilders.IAbstractionPathBuilder;
import soot.jimple.infoflow.data.pathBuilders.IPathBuilderFactory;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler2;
import soot.jimple.infoflow.problems.BackwardsInfoflowProblem;
import soot.jimple.infoflow.problems.InfoflowProblem;
import soot.jimple.infoflow.problems.TaintPropagationResults;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.IMemoryManager;
import soot.jimple.infoflow.solver.cfg.BackwardsInfoflowCFG;
import soot.jimple.infoflow.solver.fastSolver.BackwardsInfoflowSolver;
import soot.jimple.infoflow.solver.fastSolver.InfoflowSolver;
import soot.jimple.infoflow.source.ISourceSinkManager;
import soot.jimple.infoflow.util.SystemClassHandler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressWarnings("PackageAccessibility")
public class SplitInfoflow extends Infoflow {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private InfoflowManager manager;
    private InfoflowProblem forwardProblem;
    private BackwardsInfoflowProblem backProblem;
    private InfoflowSolver forwardSolver;
    private CountingThreadPoolExecutor executor;
    private InfoflowSolver backSolver;
    private int numThreads;
    private ISourceSinkManager sourcesSinks;

    public SplitInfoflow(String androidPath, boolean forceAndroidJar, BiDirICFGFactory icfgFactory,
                         IPathBuilderFactory pathBuilderFactory) {
        super(androidPath, forceAndroidJar, icfgFactory, pathBuilderFactory);
    }

    @Override
    protected void runAnalysis(ISourceSinkManager sourcesSinks, Set<String> additionalSeeds) {
        getConfig().setLogSourcesAndSinks(true);
        this.sourcesSinks = sourcesSinks;
        // Clear the data from previous runs
        maxMemoryConsumption = -1;
        results = null;

        // Some configuration options do not really make sense in combination
        if (config.getEnableStaticFieldTracking()
                && InfoflowConfiguration.getAccessPathLength() == 0)
            throw new RuntimeException("Static field tracking must be disabled "
                    + "if the access path length is zero");
        if (InfoflowConfiguration.getAccessPathLength() < 0)
            throw new RuntimeException("The access path length may not be negative");

        // Clear the base registrations from previous runs
        AccessPathFactory.v().clearBaseRegister();

        // Build the callgraph
        long beforeCallgraph = System.nanoTime();
        constructCallgraph();
        logger.info("Callgraph construction took " + (System.nanoTime() - beforeCallgraph) / 1E9
                + " seconds");

        // Perform constant propagation and remove dead code
        if (config.getCodeEliminationMode() != InfoflowConfiguration.CodeEliminationMode.NoCodeElimination) {
            long currentMillis = System.nanoTime();
            eliminateDeadCode(sourcesSinks);
            logger.info("Dead code elimination took " + (System.nanoTime() - currentMillis) / 1E9
                    + " seconds");
        }
        if (config.getCallgraphAlgorithm() != InfoflowConfiguration.CallgraphAlgorithm.OnDemand)
            logger.info("Callgraph has {} edges", Scene.v().getCallGraph().size());

        if (!config.isTaintAnalysisEnabled()) {
            return;
        }
        logger.info("Starting Taint Analysis");
        iCfg = icfgFactory.buildBiDirICFG(config.getCallgraphAlgorithm(),
                config.getEnableExceptionTracking());



        // Print our configuration
        config.printSummary();
        int sinkCount = 0;
        logger.info("Looking for sources and sinks...");

        for (SootMethod sm : getMethodsForSeeds(iCfg))
            sinkCount += scanMethodForSourcesSinks(sourcesSinks, null, sm);

        if (additionalSeeds != null)
            for (String meth : additionalSeeds) {
                SootMethod m = Scene.v().getMethod(meth);
                if (!m.hasActiveBody()) {
                    logger.warn("Seed method {} has no active body", m);
                    continue;
                }
                collectedSources.add(m.getActiveBody().getUnits().getFirst());
            }

        // Report on the sources and sinks we have found
        if (collectedSources.isEmpty()) {
            logger.error("No sources found, aborting analysis");
            return;
        }

        if (sinkCount == 0) {
            logger.error("No sinks found, aborting analysis");
            return;
        }
        logger.info("Source lookup done, found {} sources and {} sinks.", collectedSources.size(),
                sinkCount);
    }


    public void runAnalysisSecondStep(){
        // Initialize the memory manager
        FlowDroidMemoryManager.PathDataErasureMode erasureMode = FlowDroidMemoryManager.PathDataErasureMode.EraseAll;
        if (pathBuilderFactory.isContextSensitive())
            erasureMode = FlowDroidMemoryManager.PathDataErasureMode.KeepOnlyContextData;
        if (pathBuilderFactory.supportsPathReconstruction())
            erasureMode = FlowDroidMemoryManager.PathDataErasureMode.EraseNothing;
        IMemoryManager<Abstraction> memoryManager = new FlowDroidMemoryManager(false,
                erasureMode);
        numThreads = Runtime.getRuntime().availableProcessors();
        executor = createExecutor(numThreads);
        manager = new InfoflowManager(config, null, iCfg, sourcesSinks,
                taintWrapper, hierarchy);

        backProblem = null;
        InfoflowManager backwardsManager = null;
        backSolver = null;
        final IAliasingStrategy aliasingStrategy;
        switch (getConfig().getAliasingAlgorithm()) {
            case FlowSensitive:
                backwardsManager = new InfoflowManager(config, null,
                        new BackwardsInfoflowCFG(iCfg), sourcesSinks, taintWrapper, hierarchy);
                backProblem = new BackwardsInfoflowProblem(backwardsManager);
                backSolver = new BackwardsInfoflowSolver(backProblem, executor);
                backSolver.setMemoryManager(memoryManager);
                backSolver.setJumpPredecessors(!pathBuilderFactory.supportsPathReconstruction());
//				backSolver.setEnableMergePointChecking(true);

                aliasingStrategy = new FlowSensitiveAliasStrategy(iCfg, backSolver);
                break;
            case PtsBased:
                backProblem = null;
                backSolver = null;
                aliasingStrategy = new PtsBasedAliasStrategy(iCfg);
                break;
            default:
                throw new RuntimeException("Unsupported aliasing algorithm");
        }

        // Get the zero fact
        Abstraction zeroValue = backProblem != null
                ? backProblem.createZeroValue() : null;
        forwardProblem = new InfoflowProblem(manager,
                aliasingStrategy, zeroValue);

        // Set the options
        forwardSolver = new InfoflowSolver(forwardProblem, executor);
        aliasingStrategy.setForwardSolver(forwardSolver);
        manager.setForwardSolver(forwardSolver);
        if (backwardsManager != null)
            backwardsManager.setForwardSolver(forwardSolver);

        forwardSolver.setMemoryManager(memoryManager);
        forwardSolver.setJumpPredecessors(!pathBuilderFactory.supportsPathReconstruction());
//		forwardSolver.setEnableMergePointChecking(true);

        forwardProblem.setTaintPropagationHandler(taintPropagationHandler);
        forwardProblem.setTaintWrapper(taintWrapper);
        if (nativeCallHandler != null)
            forwardProblem.setNativeCallHandler(nativeCallHandler);

        if (backProblem != null) {
            backProblem.setForwardSolver(forwardSolver);
            backProblem.setTaintPropagationHandler(backwardsPropagationHandler);
            backProblem.setTaintWrapper(taintWrapper);
            if (nativeCallHandler != null)
                backProblem.setNativeCallHandler(nativeCallHandler);
            backProblem.setActivationUnitsToCallSites(forwardProblem);
        }


        if (config.getFlowSensitiveAliasing() && !aliasingStrategy.isFlowSensitive())
            logger.warn("Trying to use a flow-sensitive aliasing with an "
                    + "aliasing strategy that does not support this feature");

        for (Unit u :collectedSources){
            forwardProblem.addInitialSeeds(u,Collections.singleton(forwardProblem.zeroValue()));
        }
        if (taintWrapper != null)
            taintWrapper.initialize(manager);
        if (nativeCallHandler != null)
            nativeCallHandler.initialize(manager);
        // Register the handler for interim results
        TaintPropagationResults propagationResults = forwardProblem.getResults();
        final CountingThreadPoolExecutor resultExecutor = createExecutor(numThreads);
        final IAbstractionPathBuilder builder = pathBuilderFactory.createPathBuilder(
                resultExecutor, iCfg);

        if (config.getIncrementalResultReporting()) {
            // Create the path builder
            this.results = new InfoflowResults();
            propagationResults.addResultAvailableHandler(new TaintPropagationResults.OnTaintPropagationResultAdded() {


                @Override
                public boolean onResultAvailable(AbstractionAtSink abs) {
                    builder.addResultAvailableHandler(new IAbstractionPathBuilder.OnPathBuilderResultAvailable() {

                        @Override
                        public void onResultAvailable(ResultSourceInfo source, ResultSinkInfo sink) {
                            // Notify our external handlers
                            for (ResultsAvailableHandler handler : onResultsAvailable) {
                                if (handler instanceof ResultsAvailableHandler2) {
                                    ResultsAvailableHandler2 handler2 = (ResultsAvailableHandler2) handler;
                                    handler2.onSingleResultAvailable(source, sink);
                                }
                            }
                            results.addResult(sink, source);
                        }

                    });

                    // Compute the result paths
                    builder.computeTaintPaths(Collections.singleton(abs));
                    return true;
                }

            });
        }

        forwardSolver.solve();
        maxMemoryConsumption = Math.max(maxMemoryConsumption, getUsedMemory());

        // Not really nice, but sometimes Heros returns before all
        // executor tasks are actually done. This way, we give it a
        // chance to terminate gracefully before moving on.
        int terminateTries = 0;
        while (terminateTries < 10) {
            if (executor.getActiveCount() != 0 || !executor.isTerminated()) {
                terminateTries++;
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    logger.error("Could not wait for executor termination", e);
                }
            }
            else
                break;
        }
        if (executor.getActiveCount() != 0 || !executor.isTerminated())
            logger.error("Executor did not terminate gracefully");

        // Print taint wrapper statistics
        if (taintWrapper != null) {
            logger.info("Taint wrapper hits: " + taintWrapper.getWrapperHits());
            logger.info("Taint wrapper misses: " + taintWrapper.getWrapperMisses());
        }

        Set<AbstractionAtSink> res = propagationResults.getResults();

        // We need to prune access paths that are entailed by another one
        for (Iterator<AbstractionAtSink> absAtSinkIt = res.iterator(); absAtSinkIt.hasNext(); ) {
            AbstractionAtSink curAbs = absAtSinkIt.next();
            for (AbstractionAtSink checkAbs : res)
                if (checkAbs != curAbs
                        && checkAbs.getSinkStmt() == curAbs.getSinkStmt()
                        && checkAbs.getAbstraction().isImplicit() == curAbs.getAbstraction().isImplicit()
                        && checkAbs.getAbstraction().getSourceContext() == curAbs.getAbstraction().getSourceContext())
                    if (checkAbs.getAbstraction().getAccessPath().entails(
                            curAbs.getAbstraction().getAccessPath())) {
                        absAtSinkIt.remove();
                        break;
                    }
        }

        logger.info("IFDS problem with {} forward and {} backward edges solved, "
                        + "processing {} results...", forwardSolver.propagationCount,
                backSolver == null ? 0 : backSolver.propagationCount,
                res == null ? 0 : res.size());

        // Force a cleanup. Everything we need is reachable through the
        // results set, the other abstractions can be killed now.
        maxMemoryConsumption = Math.max(maxMemoryConsumption, getUsedMemory());
        forwardSolver.cleanup();
        if (backSolver != null) {
            backSolver.cleanup();
//            backSolver = null;
//            backProblem = null;
        }
//        forwardSolver = null;
//        forwardProblem = null;
        Runtime.getRuntime().gc();

        if (!config.getIncrementalResultReporting()) {
            builder.computeTaintPaths(res);
            if (this.results == null)
                this.results = builder.getResults();
            else
                this.results.addAll(builder.getResults());
        }

        // Wait for the path builders to terminate
        try {
            resultExecutor.awaitCompletion();
        } catch (InterruptedException e) {
            logger.error("Could not wait for executor termination", e);
        }

        if (config.getIncrementalResultReporting()) {
            // After the last intermediate result has been computed, we need to
            // re-process those abstractions that received new neighbors in the
            // meantime
            builder.runIncrementalPathCompuation();

            try {
                resultExecutor.awaitCompletion();
            } catch (InterruptedException e) {
                logger.error("Could not wait for executor termination", e);
            }
        }
        resultExecutor.shutdown();

        if (results == null || results.getResults().isEmpty())
            logger.warn("No results found.");
        else for (ResultSinkInfo sink : results.getResults().keySet()) {
            logger.info("The sink {} in method {} was called with values from the following sources:",
                    sink, iCfg.getMethodOf(sink.getSink()).getSignature() );
            for (ResultSourceInfo source : results.getResults().get(sink)) {
                logger.info("- {} in method {}",source, iCfg.getMethodOf(source.getSource()).getSignature());
                if (source.getPath() != null) {
                    logger.info("\ton Path: ");
                    for (Unit p : source.getPath()) {
                        logger.info("\t -> " + iCfg.getMethodOf(p));
                        logger.info("\t\t -> " + p);
                    }
                }
            }
        }

        for (ResultsAvailableHandler handler : onResultsAvailable)
            handler.onResultsAvailable(iCfg, results);

        if (config.getWriteOutputFiles())
            PackManager.v().writeOutput();

        maxMemoryConsumption = Math.max(maxMemoryConsumption, getUsedMemory());
        System.out.println("Maximum memory consumption: " + maxMemoryConsumption / 1E6 + " MB");
    }


    @Override
    protected int scanMethodForSourcesSinks(
            final ISourceSinkManager sourcesSinks,
            InfoflowProblem forwardProblem,
            SootMethod m) {
        if (getConfig().getLogSourcesAndSinks() && collectedSources == null) {
            collectedSources = new HashSet<>();
            collectedSinks = new HashSet<>();
        }

        int sinkCount = 0;
        if (m.hasActiveBody()) {
            // Check whether this is a system class we need to ignore
            final String className = m.getDeclaringClass().getName();
            if (config.getIgnoreFlowsInSystemPackages()
                    && SystemClassHandler.isClassInSystemPackage(className))
                return sinkCount;

            // Look for a source in the method. Also look for sinks. If we
            // have no sink in the program, we don't need to perform any
            // analysis
            PatchingChain<Unit> units = m.getActiveBody().getUnits();
            for (Unit u : units) {
                Stmt s = (Stmt) u;
                if (sourcesSinks.getSourceInfo(s, iCfg) != null) {
                    if (getConfig().getLogSourcesAndSinks())
                        collectedSources.add(s);
                    logger.debug("Source found: {}", u);
                }
                if (sourcesSinks.isSink(s, iCfg, null)) {
                    sinkCount++;
                    if (getConfig().getLogSourcesAndSinks())
                        collectedSinks.add(s);
                    logger.debug("Sink found: {}", u);
                }
            }

        }
        return sinkCount;
    }
}
