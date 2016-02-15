package de.tud.cs.peaks.osgi.soot.impl;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tud.cs.peaks.osgi.soot.api.SootBundleResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import de.tud.cs.peaks.sootconfig.SootRun;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

@DependsOn({})
public class SootService extends AbstractSootService {
    private static final String NAME = "soot";

    public SootService(BundleContext context) throws IllegalClassFormatException {
        super(context);
    }

    @Override
    public SootBundleConfig parseConfig(Object conf) {
        if (conf instanceof String[]) {
            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
            AnalysisTarget target = new AnalysisTarget().processPath(((String[]) conf)[0]);
            return new SootBundleConfig(options, target);
        } else if (conf instanceof SootBundleConfig) {
            return (SootBundleConfig) conf;
        }
        throw new IllegalArgumentException("Could not create config");
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public SootBundleResult runAnalysis(SootBundleConfig config,
                                        Map<Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>>,
                                                IAnalysisResult> previousResults) {
        System.out.println("Prep. Soot!");

        SootRun sootRun = new SootRun(config.getFluentOptions(), config.getAnalysisTarget());
        SootResult res = sootRun.perform();
        System.out.println(res.getCompleteOutput());
        return new SootBundleResult(res);


    }

    @Override
    public IAnalysisConfig convertConfig(IAnalysisConfig config,
                                         Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceClass) {
        return config;
    }

}
