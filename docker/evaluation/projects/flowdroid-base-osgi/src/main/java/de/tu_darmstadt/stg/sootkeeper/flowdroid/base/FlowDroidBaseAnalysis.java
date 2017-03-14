package de.tu_darmstadt.stg.sootkeeper.flowdroid.base;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import org.xmlpull.v1.XmlPullParserException;
import soot.G;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.infoflow.InfoflowManager;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.data.Abstraction;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.ipc.IIPCManager;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.results.xml.InfoflowResultsSerializer;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
import soot.jimple.infoflow.util.SystemClassHandler;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("Duplicates")
public class FlowDroidBaseAnalysis extends AbstractAnalysisService<IntermediateResult, FlowDroidConfig> {
    private static final String NAME = "FlowdroidBase";


    /**
     * Constructor of the AnalysisService. Also checks the concrete service class layout.
     *
     * @param context the context of the bundle this service belongs to.
     * @throws IllegalClassFormatException when the concrete service does not have a @DependsOn annotation.
     * @throws IllegalStateException       if an AnalysisService required by the @DependsOn annotation is not registered in the context.
     */
    protected FlowDroidBaseAnalysis(BundleContext context) throws IllegalClassFormatException, IllegalStateException {
        super(context);
    }

    private static ITaintPropagationWrapper createLibrarySummaryTW(String summaryPath)
            throws IOException {
        try {
            Class clzLazySummary = Class.forName("soot.jimple.infoflow.methodSummary.data.provider.LazySummaryProvider");
            Class itfLazySummary = Class.forName("soot.jimple.infoflow.methodSummary.data.provider.IMethodSummaryProvider");

            Object lazySummary = clzLazySummary.getConstructor(File.class).newInstance(new File(summaryPath));

            ITaintPropagationWrapper summaryWrapper = (ITaintPropagationWrapper) Class.forName
                    ("soot.jimple.infoflow.methodSummary.taintWrappers.SummaryTaintWrapper").getConstructor
                    (itfLazySummary).newInstance(lazySummary);

            ITaintPropagationWrapper systemClassWrapper = new ITaintPropagationWrapper() {

                private ITaintPropagationWrapper wrapper = new EasyTaintWrapper("EasyTaintWrapperSource.txt");

                private boolean isSystemClass(Stmt stmt) {
                    if (stmt.containsInvokeExpr())
                        return SystemClassHandler.isClassInSystemPackage(
                                stmt.getInvokeExpr().getMethod().getDeclaringClass().getName());
                    return false;
                }

                @Override
                public boolean supportsCallee(Stmt callSite) {
                    return isSystemClass(callSite) && wrapper.supportsCallee(callSite);
                }

                @Override
                public boolean supportsCallee(SootMethod method) {
                    return SystemClassHandler.isClassInSystemPackage(method.getDeclaringClass().getName())
                            && wrapper.supportsCallee(method);
                }

                @Override
                public boolean isExclusive(Stmt stmt, Abstraction taintedPath) {
                    return isSystemClass(stmt) && wrapper.isExclusive(stmt, taintedPath);
                }

                @Override
                public void initialize(InfoflowManager manager) {
                    wrapper.initialize(manager);
                }

                @Override
                public int getWrapperMisses() {
                    return 0;
                }

                @Override
                public int getWrapperHits() {
                    return 0;
                }

                @Override
                public Set<Abstraction> getTaintsForMethod(Stmt stmt, Abstraction d1,
                                                           Abstraction taintedPath) {
                    if (!isSystemClass(stmt))
                        return null;
                    return wrapper.getTaintsForMethod(stmt, d1, taintedPath);
                }

                @Override
                public Set<Abstraction> getAliasesForMethod(Stmt stmt, Abstraction d1,
                                                            Abstraction taintedPath) {
                    if (!isSystemClass(stmt))
                        return null;
                    return wrapper.getAliasesForMethod(stmt, d1, taintedPath);
                }

            };

            Method setFallbackMethod = summaryWrapper.getClass().getMethod("setFallbackTaintWrapper",
                    ITaintPropagationWrapper.class);
            setFallbackMethod.invoke(summaryWrapper, systemClassWrapper);

            return summaryWrapper;
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            System.err.println("Could not find library summary classes: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (InvocationTargetException ex) {
            System.err.println("Could not initialize library summaries: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        } catch (IllegalAccessException | InstantiationException ex) {
            System.err.println("Internal error in library summary initialization: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IntermediateResult runAnalysis(FlowDroidConfig flowDroidConfig, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> previousResults) {
        try {
            if (flowDroidConfig == null) {
                System.out.println("Configuration was invalid aborting analysis");
                return null;
            }
            G.GlobalObjectGetter getter = new G.GlobalObjectGetter() {
                G g = new G();

                @Override
                public G getG() {
                    return g;
                }

                @Override
                public void reset() {
                    g = new G();
                }
            };
            G.setGlobalObjectGetter(getter);
            final long beforeRun = System.nanoTime();
            IIPCManager ipcManager = flowDroidConfig.getIpcManager();
            String androidJar = flowDroidConfig.getAndroidJarPath();
            String fileName = flowDroidConfig.getApkFile();
            final SetupApplication app;
            if (null == ipcManager) {
                app = new SetupApplication(androidJar, fileName, "", null);
            } else {
                app = new SetupApplication(androidJar, fileName, ipcManager);
            }
            String summaryPath = flowDroidConfig.getSummaryPath();
            boolean noTaintWrapper = flowDroidConfig.isNoTaintWrapper();
            // Set configuration object
            app.setConfig(flowDroidConfig.getConfig());
            if (noTaintWrapper)
                app.setSootConfig(options -> options.set_include_all(true));

            final ITaintPropagationWrapper taintWrapper;
            if (noTaintWrapper)
                taintWrapper = null;
            else if (summaryPath != null && !summaryPath.isEmpty()) {
                System.out.println("Using the StubDroid taint wrapper");
                taintWrapper = createLibrarySummaryTW(summaryPath);
                if (taintWrapper == null) {
                    System.err.println("Could not initialize StubDroid");
                    return null;
                }
            } else {
                final EasyTaintWrapper easyTaintWrapper;
                File twSourceFile = new File("../soot-infoflow/EasyTaintWrapperSource.txt");
                if (twSourceFile.exists())
                    easyTaintWrapper = new EasyTaintWrapper(twSourceFile);
                else {
                    twSourceFile = new File("EasyTaintWrapperSource.txt");
                    if (twSourceFile.exists())
                        easyTaintWrapper = new EasyTaintWrapper(twSourceFile);
                    else {
                        System.err.println("Taint wrapper definition file not found at "
                                + twSourceFile.getAbsolutePath());
                        return null;
                    }
                }
                easyTaintWrapper.setAggressiveMode(flowDroidConfig.isAggressiveTaintWrapper());
                taintWrapper = easyTaintWrapper;
            }
            app.setTaintWrapper(taintWrapper);
            app.calculateSourcesSinksEntrypoints("SourcesAndSinks.txt");

            if (FlowDroidConfig.DEBUG) {
                app.printEntrypoints();
                app.printSinks();
                app.printSources();
            }
            SplitInfoflow infoflow = new SplitInfoflow(androidJar, new File(androidJar).isFile(), null,
                    new DefaultPathBuilderFactory(flowDroidConfig.getConfig().getPathBuilder(),
                            flowDroidConfig.getConfig().getComputeResultPaths()));
            System.out.println("Running data flow analysis...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler(flowDroidConfig.getResultFilePath()), infoflow);

            if (flowDroidConfig.getConfig().getLogSourcesAndSinks()) {
                if (!app.getCollectedSources().isEmpty()) {
                    System.out.println("Collected sources:");
                    for (Unit s : app.getCollectedSources())
                        System.out.println("\t" + s);
                }
                if (!app.getCollectedSinks().isEmpty()) {
                    System.out.println("Collected sinks:");
                    for (Stmt s : app.getCollectedSinks())
                        System.out.println("\t" + s);
                }
            }
            return new IntermediateResult(getter, infoflow);
        } catch (IOException ex) {
            System.err.println("Could not read file: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (XmlPullParserException ex) {
            System.err.println("Could not read Android manifest file: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public IAnalysisConfig convertConfig(FlowDroidConfig config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {
        return null;
    }

    @Override
    public FlowDroidConfig parseConfig(String[] config) {
        return null;
    }

    @Override
    public boolean shouldBeHidden() {
        return true;
    }

    @Override
    protected List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.emptyList();
    }

    @SuppressWarnings({"PackageAccessibility", "Duplicates"})
    private static final class MyResultsAvailableHandler implements
            ResultsAvailableHandler {
        private final String resultFilePath;
        private final BufferedWriter wr;


        private MyResultsAvailableHandler(String resultFilePath) {
            this(resultFilePath, null);
        }

        private MyResultsAvailableHandler(String resultFilePath, BufferedWriter wr) {
            this.resultFilePath = resultFilePath;
            this.wr = wr;
        }

        @Override
        public void onResultsAvailable(
                IInfoflowCFG cfg, InfoflowResults results) {
            // Dump the results
            if (results == null) {
                print("No results found.");
            } else {
                // Report the results
                for (ResultSinkInfo sink : results.getResults().keySet()) {
                    print("Found a flow to sink " + sink + ", from the following sources:");
                    for (ResultSourceInfo source : results.getResults().get(sink)) {
                        print("\t- " + source.getSource() + " (in "
                                + cfg.getMethodOf(source.getSource()).getSignature() + ")");
                        if (source.getPath() != null)
                            print("\t\ton Path " + Arrays.toString(source.getPath()));
                    }
                }

                // Serialize the results if requested
                // Write the results into a file if requested
                if (resultFilePath != null && !resultFilePath.isEmpty()) {
                    InfoflowResultsSerializer serializer = new InfoflowResultsSerializer(cfg);
                    try {
                        serializer.serialize(results, resultFilePath);
                    } catch (FileNotFoundException ex) {
                        System.err.println("Could not write data flow results to file: " + ex.getMessage());
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    } catch (XMLStreamException ex) {
                        System.err.println("Could not write data flow results to file: " + ex.getMessage());
                        ex.printStackTrace();
                        throw new RuntimeException(ex);
                    }
                }
            }

        }

        private void print(String string) {
            try {
                System.out.println(string);
                if (wr != null)
                    wr.write(string + "\n");
            } catch (IOException ex) {
                // ignore
            }
        }

    }
}
