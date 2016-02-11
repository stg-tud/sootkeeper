package de.tud.cs.peaks.osgi.soot.impl;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootService;
import de.tud.cs.peaks.osgi.soot.api.SootConfig;
import de.tud.cs.peaks.osgi.soot.api.SootResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootRun;
import org.osgi.framework.BundleContext;
import soot.G;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

@DependsOn({})
public class SootService extends AbstractSootService {
    private static final String NAME = "soot";

    public SootService(BundleContext context) throws IllegalClassFormatException {
        super(context);
    }

    @Override
    public SootConfig parseConfig(String[] conf) {
        return new SootConfig(42);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public SootResult runAnalysis(IAnalysisConfig config,
                                  Map<Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>>,
                                          IAnalysisResult> previousResults) {
        System.out.println("Prep. Soot!");
        try {
            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
            AnalysisTarget target = new AnalysisTarget().processPath("");
            SootRun sootRun = new SootRun(options, target);
            System.out.println("Start Soot");
            de.tud.cs.peaks.sootconfig.SootResult res = sootRun.perform();
            System.out.println("Soot has run:");
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SootResult r = new SootResult(((SootConfig) config).getValue());
        return r;
    }

    @Override
    public IAnalysisConfig convertConfig(IAnalysisConfig config,
                                         Class<? extends AbstractAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceClass) {
        return config;
    }

}
