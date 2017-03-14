package de.tud.cs.peaks.osgi.peaks.analysis.staticUsage;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.peaks.analysis.staticUsage.api.StaticUsageResult;
import de.tud.cs.peaks.osgi.peaks.analysis.staticUsage.api.StaticUsageService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;

public class Activator extends AbstractAnalysisActivator<StaticUsageResult, SootBundleConfig> {

    @Override
    public AbstractAnalysisService<StaticUsageResult, SootBundleConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new StaticUsageService(bundleContext);
    }
}
