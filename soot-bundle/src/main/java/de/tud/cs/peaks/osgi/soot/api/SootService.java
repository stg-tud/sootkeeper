package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import de.tud.cs.peaks.sootconfig.SootRun;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

@DependsOn({})
public class SootService extends AbstractAnalysisService<SootBundleResult, SootBundleConfig> {
    private static final String NAME = "soot";
    private static final Object mutex = new Object();

    public SootService(BundleContext context) throws IllegalClassFormatException {
        super(context);
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
            System.out.println("Prep. Soot!");
            SootRun sootRun = new SootRun(config.getFluentOptions(), config.getAnalysisTarget());
            SootResult res = sootRun.perform();
            System.out.println(res.getCompleteOutput());
            return new SootBundleResult(res);
        }

    }

    @Override
    public IAnalysisConfig convertConfig(SootBundleConfig config,
                                         Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {
        return config;
    }

}
