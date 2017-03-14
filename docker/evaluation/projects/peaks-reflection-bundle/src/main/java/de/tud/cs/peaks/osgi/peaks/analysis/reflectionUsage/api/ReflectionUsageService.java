package de.tud.cs.peaks.osgi.peaks.analysis.reflectionUsage.api;

import de.tud.cs.peaks.analysis.reflectionUsage.ReflectionAnalysis;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootAnalysis;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tud.cs.peaks.osgi.soot.api.SootService;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import org.osgi.framework.BundleContext;
import soot.G;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ReflectionUsageService extends AbstractSootAnalysis<ReflectionUsageResult, SootBundleConfig> {
    private static final String NAME = "peaks-reflection";

    public ReflectionUsageService(BundleContext context) throws IllegalClassFormatException {
        super(context);
    }

    @Override
    protected List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.singletonList(SootService.class);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IAnalysisConfig convertConfig(SootBundleConfig config, Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> aClass) {
        return config;
    }

    @Override
    protected ReflectionUsageResult runSootBasedAnalysis(SootBundleConfig config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map, SootResult sootResult) {
        ReflectionAnalysis analysis = new ReflectionAnalysis();
        AnalysisResult result = analysis.performAnalysis(sootResult.getScene().getCallGraph());
        return new ReflectionUsageResult(result);
    }

    @Override
    public SootBundleConfig parseConfig(String[] conf) {
        if (conf.length > 0) {
            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
            AnalysisTarget target = new AnalysisTarget().processPath(conf[0]);
            return new SootBundleConfig(options, target);
        }
        throw new IllegalArgumentException("Could not create config");
    }
}
