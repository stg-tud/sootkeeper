package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import de.tud.cs.peaks.sootconfig.SootRun;
import org.osgi.framework.BundleContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SootService extends AbstractAnalysisService<SootBundleResult, SootBundleConfig> {
    private static final String NAME = "soot";
    private static final Object mutex = new Object();

    public SootService(BundleContext context) {
        super(context);
    }

    @Override
    public List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.emptyList();
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


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public SootBundleResult runAnalysis(SootBundleConfig config,
                                        Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>,
                                                IAnalysisResult> previousResults) {
        synchronized (mutex) {
            SootRun sootRun = new SootRun(config.getFluentOptions(), config.getAnalysisTarget());
            SootResult res = sootRun.perform();
            return new SootBundleResult(res);
        }

    }

    @Override
    public IAnalysisConfig convertConfig(SootBundleConfig config,
                                         Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {
        return config;
    }

}
