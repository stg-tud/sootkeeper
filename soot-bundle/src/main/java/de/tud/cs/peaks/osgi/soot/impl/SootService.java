package de.tud.cs.peaks.osgi.soot.impl;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootService;
import de.tud.cs.peaks.osgi.soot.api.SootConfig;
import de.tud.cs.peaks.osgi.soot.api.SootResult;
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
    public SootConfig parseConfig(Object conf) {
        if (conf instanceof Integer) {
            return new SootConfig((Integer) conf);
        } else if (conf instanceof String) {
            if (((String) conf).isEmpty()){
                return new SootConfig(5);
            }
            return new SootConfig(Integer.valueOf((String) conf));
        }
        throw new IllegalArgumentException("Could not create Config from " + conf);
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
            soot.Main.main(new String[]{"-w", "-allow-phantom-refs", "-process-path", "/home/torun/Dropbox/intelli/peaks/input/log4j-1.2.17.jar"});
//            FluentOptions options = new FluentOptions().wholeProgramAnalysis().keepLineNumbers().allowPhantomReferences();
//            AnalysisTarget target = new AnalysisTarget().processPath("/Users/floriankuebler/Desktop/securibench-91a/");
//            SootRun sootRun = new SootRun(options, target);
//            System.out.println("Start Soot");
//            de.tud.cs.peaks.sootconfig.SootResult res = sootRun.perform();
//            System.out.println("Soot has run:");
//            System.out.println(res);
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
