package de.tud.cs.peaks.osgi.hello;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Collections;
import java.util.List;

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
    public List<AbstractAnalysisService<IntegerResult, IntegerConfig>> getAnalysisServices(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException {
        return Collections.singletonList(new IntegerService(bundleContext));
    }


}
