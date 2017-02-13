package de.tud.cs.peaks.osgi.hello.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class IntegerService extends AbstractAnalysisService<IntegerResult, IntegerConfig> {
    private static final String NAME = "IntegerAnalysis";

    public IntegerService(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
        super(context);
    }

    @Override
    protected List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.emptyList();
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IntegerResult runAnalysis(IntegerConfig config,
                                     Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>,
                                             IAnalysisResult> previousResults) {
        System.out.println("Start Computing");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Computation done!");
        IntegerResult r = new IntegerResult(config.getValue());
        System.out.println(r);
        return r;
    }

    @Override
    public IAnalysisConfig convertConfig(IntegerConfig config,
                                         Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {
        return config;
    }

    @Override
    public IntegerConfig parseConfig(String[] config) {
        if (((String[]) config).length == 1) {
            return new IntegerConfig(Integer.parseInt(((String[]) config)[0]));
        }
        throw new IllegalArgumentException("Please provide exactly 1 Integer");
    }
}
