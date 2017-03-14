package de.tu_darmstadt.stg.sootkeeper.flowdroid.base;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;

public class FlowdroidBaseActivator extends AbstractAnalysisActivator {
    @Override
    public AbstractAnalysisService getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new FlowDroidBaseAnalysis(bundleContext);
    }
}
