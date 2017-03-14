package de.tud.cs.peaks.osgi.peaks.analysis.reflectionUsage;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.peaks.analysis.reflectionUsage.api.ReflectionUsageResult;
import de.tud.cs.peaks.osgi.peaks.analysis.reflectionUsage.api.ReflectionUsageService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;

public class Activator extends AbstractAnalysisActivator<ReflectionUsageResult, SootBundleConfig> {

    @Override
    public AbstractAnalysisService<ReflectionUsageResult, SootBundleConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new ReflectionUsageService(bundleContext);
    }
}
