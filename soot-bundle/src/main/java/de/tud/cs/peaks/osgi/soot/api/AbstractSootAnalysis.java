package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.sootconfig.SootResult;
import org.osgi.framework.BundleContext;
import soot.G;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

public abstract class AbstractSootAnalysis<Result extends IAnalysisResult, Config extends IAnalysisConfig> extends AbstractAnalysisService<Result, Config> {

    protected AbstractSootAnalysis(BundleContext context) throws IllegalClassFormatException {
        super(context);
    }

    protected abstract Result runSootBasedAnalysis(Config config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map, SootResult soootResult);

    @Override
    public Result runAnalysis(Config config,Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map) {
        final SootResult soootResult = ((SootBundleResult) map.get(SootService.class)).getSootResult();

        //TODO
        G.setGlobalObjectGetter(new G.GlobalObjectGetter() {
            @Override
            public G getG() {
                return soootResult.getSootGlobal();
            }

            @Override
            public void reset() {
                //TODO
            }
        });

        return runSootBasedAnalysis(config, map, soootResult);
    }



}
