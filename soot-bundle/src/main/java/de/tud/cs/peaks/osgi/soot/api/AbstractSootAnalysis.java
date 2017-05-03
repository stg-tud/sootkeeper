package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.sootconfig.SootResult;
import org.osgi.framework.BundleContext;
import soot.G;

import java.util.Map;

public abstract class AbstractSootAnalysis<Result extends IAnalysisResult, Config extends IAnalysisConfig> extends AbstractAnalysisService<Result, Config> {

    protected AbstractSootAnalysis(BundleContext context) {
        super(context);
    }

    protected abstract Result runSootBasedAnalysis(Config config, Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map, SootResult soootResult);

    @Override
    public Result runAnalysis(Config config,Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> map) {
        final SootResult sootResult = ((SootBundleResult) map.get(SootService.class)).getSootResult();

        //TODO
        G.setGlobalObjectGetter(new G.GlobalObjectGetter() {
            @Override
            public G getG() {
                return sootResult.getSootGlobal();
            }

            @Override
            public void reset() {
                //TODO
            }
        });

        return runSootBasedAnalysis(config, map, sootResult);
    }



}
