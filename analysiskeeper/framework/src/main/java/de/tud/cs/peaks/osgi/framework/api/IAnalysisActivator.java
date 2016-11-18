package de.tud.cs.peaks.osgi.framework.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.lang.instrument.IllegalClassFormatException;
import java.util.List;

/**
 * Provides an BundleActivator Interface specific to Analyses
 *
 * @author Florian Kuebler
 * @see IAnalysisService
 */
public interface IAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig> extends BundleActivator {

    /**
     * Returns a Analysis Service Object which will be registered in OSGi
     *
     * @param bundleContext the current OSGi BundleContext
     * @return the corresponding analysisService
     * @throws IllegalClassFormatException when the concrete service does not have a {@link de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn} annotation.
     * @throws IllegalStateException       if an AnalysisService required by the {@link de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn} annotation is not registered in the context.
     * @see de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn
     */
    List<AbstractAnalysisService<Result, Config>> getAnalysisServices(BundleContext bundleContext) throws IllegalStateException, IllegalClassFormatException;

}
