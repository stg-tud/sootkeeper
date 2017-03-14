package de.tu_darmstadt.stg.sootkeeper.flowdroid;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.LinkedList;
import java.util.List;


public class FlowdroidActivator extends AbstractAnalysisActivator {
    @Override
    public AbstractAnalysisService getAnalysisService(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return new FlowDroidAnalysisService((bundleContext));
    }
}
