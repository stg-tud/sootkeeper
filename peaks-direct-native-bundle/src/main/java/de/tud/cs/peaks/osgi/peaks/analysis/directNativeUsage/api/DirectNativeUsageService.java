package de.tud.cs.peaks.osgi.peaks.analysis.directNativeUsage.api;

import de.tud.cs.peaks.analysis.directNativeUsage.DirectNativeUsage;
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
import java.util.Map;

@DependsOn({SootService.class})
public class DirectNativeUsageService extends AbstractSootAnalysis<DirectNativeResult, SootBundleConfig> {
    private static final String NAME = "direct-native";

    public DirectNativeUsageService(BundleContext context) throws IllegalClassFormatException {
        super(context);
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
    protected DirectNativeResult runSootBasedAnalysis(SootBundleConfig config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map, SootResult sootResult) {
        DirectNativeUsage analysis = new DirectNativeUsage();
        AnalysisResult result = analysis.performAnalysis(sootResult.getScene().getCallGraph());
        return new DirectNativeResult(result);
    }

    @Override
    public SootBundleConfig parseConfig(String[] conf) {
        if (conf.length > 0) {
            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
            AnalysisTarget target = new AnalysisTarget().processPath(((String[]) conf)[0]);
            return new SootBundleConfig(options, target);
        }
        throw new IllegalArgumentException("Could not create config");
    }
}