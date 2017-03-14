package de.tud.cs.peaks.osgi.peaks.analysis.transitiveNativeUsage;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.peaks.analysis.transitiveNativeUsage.api.TransitiveNativeResult;
import de.tud.cs.peaks.osgi.peaks.analysis.transitiveNativeUsage.api.TransitiveNativeUsageService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;

public class Activator extends AbstractAnalysisActivator<TransitiveNativeResult, SootBundleConfig> {

    @Override
    public AbstractAnalysisService<TransitiveNativeResult, SootBundleConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new TransitiveNativeUsageService(bundleContext);
    }
}
