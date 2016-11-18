package de.tud.cs.peaks.osgi.soot;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tud.cs.peaks.osgi.soot.api.SootBundleResult;
import de.tud.cs.peaks.osgi.soot.api.SootService;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;

/**
 * @author Florian Kuebler
 */
public class Activator extends AbstractAnalysisActivator<SootBundleResult, SootBundleConfig> {

    @Override
    public List<AbstractAnalysisService<SootBundleResult, SootBundleConfig>> getAnalysisServices(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return Collections.singletonList(new SootService(bundleContext));
    }


}
