package de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootAnalysis;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tud.cs.peaks.osgi.soot.api.SootService;
import de.tud.cs.peaks.sootconfig.*;
import de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis.LiveVariableAnalysis;
import org.osgi.framework.BundleContext;
import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class DeadCodeService extends AbstractSootAnalysis<IAnalysisResult, SootBundleConfig> {

    public DeadCodeService(BundleContext context) throws IllegalClassFormatException {
        super(context);
    }

    @Override
    protected List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.singletonList(SootService.class);
    }

    protected IAnalysisResult runSootBasedAnalysis(SootBundleConfig config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map, SootResult sootResult) {
        Transform transform = new Transform("jtp.analysis", new BodyTransformer() {

            @Override
            protected void internalTransform(Body b, String phaseName,
                                             Map<String, String> options) {
                LiveVariableAnalysis ipa = new LiveVariableAnalysis(new ExceptionalUnitGraph(b));
            }

        });

        sootResult.getSootGlobal().soot_PackManager().getPack("jtp").add(transform);


        for (SootClass sc : sootResult.getScene().getApplicationClasses()) {
            for (SootMethod m : sc.getMethods()) {
                if (m.hasActiveBody()) {
                    transform.apply(m.getActiveBody());
                }
            }
        }



        sootResult.getSootGlobal().soot_PackManager().getPack("jtp").remove("jtp.analysis");

        return null;
    }

    public String getName() {
        return "dae";
    }

    public IAnalysisConfig convertConfig(SootBundleConfig config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> aClass) {
        return config;
    }

    public SootBundleConfig parseConfig(String[] args) {
        if (args.length > 0) {
            FluentOptions fluentOptions = new FluentOptions()
                    .prependClasspath()
                    .allowPhantomReferences()
                    //.ignoreClasspathErrors()
                    .addPhaseOptions(new JimpleBodyCreationPhaseOptions().useOriginalNames());

            AnalysisTarget target = new AnalysisTarget().processPath(args[0]);
            return new SootBundleConfig(fluentOptions, target);
        }
        throw new IllegalArgumentException("Could not create config");
    }
}
