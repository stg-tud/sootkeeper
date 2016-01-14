package de.tud.cs.peaks.osgi.hello.impl;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.hello.api.AbstractIntegerService;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

@DependsOn({})
public class IntegerService extends AbstractIntegerService {
    private static final String NAME = "IntegerAnalysis";

    public IntegerService(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
        super(context);
    }

    @Override
    public IntegerConfig parseConfig(Object conf) {
        if (conf instanceof Integer) {
            return new IntegerConfig((Integer) conf);
        } else if (conf instanceof String) {
            return new IntegerConfig(Integer.valueOf((String) conf));
        }
        throw new IllegalArgumentException("Could not create Config from " + conf);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IntegerResult runAnalysis(IAnalysisConfig config,
                                     Map<Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>>,
                                             IAnalysisResult> previousResults) {
        System.out.println("Start Computing");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Computation done!");
        IntegerResult r = new IntegerResult(((IntegerConfig) config).getValue());
        System.out.println(r);
        return r;
    }

    @Override
    public IAnalysisConfig convertConfig(IAnalysisConfig config,
                                         Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceClass) {
        return config;
    }

}
