package de.tud.cs.peaks.osgi.peaks.analysis.directNativeUsage;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.peaks.analysis.directNativeUsage.api.DirectNativeResult;
import de.tud.cs.peaks.osgi.peaks.analysis.directNativeUsage.api.DirectNativeUsageService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;

public class Activator extends AbstractAnalysisActivator<DirectNativeResult, SootBundleConfig> {

    @Override
    public AbstractAnalysisService<DirectNativeResult, SootBundleConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new DirectNativeUsageService(bundleContext);
    }
}
