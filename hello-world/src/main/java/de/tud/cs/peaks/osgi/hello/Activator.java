package de.tud.cs.peaks.osgi.hello;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.hello.api.IntegerConfig;
import de.tud.cs.peaks.osgi.hello.api.IntegerResult;
import de.tud.cs.peaks.osgi.hello.api.IntegerService;
import org.osgi.framework.BundleContext;

/**
 * @author Florian Kuebler
 */
public class Activator extends AbstractAnalysisActivator<IntegerResult, IntegerConfig> {

    @Override
    public AbstractAnalysisService<IntegerResult, IntegerConfig> getAnalysisService(BundleContext bundleContext) throws IllegalStateException {
        return new IntegerService(bundleContext);
    }


}
