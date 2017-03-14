package de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tudarmstadt.stg.sootkeeper.study.deadcode.analysis.api.DeadCodeService;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;

/**
 * Created by floriankuebler on 15/11/16.
 */
    public class Activator extends AbstractAnalysisActivator<IAnalysisResult, SootBundleConfig> {
    @Override
    public AbstractAnalysisService<IAnalysisResult, SootBundleConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new DeadCodeService(bundleContext);
    }
}
