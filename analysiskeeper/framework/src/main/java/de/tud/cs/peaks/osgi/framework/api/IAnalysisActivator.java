package de.tud.cs.peaks.osgi.framework.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Provides an BundleActivator Interface specific to Analyses
 *
 * @author Florian Kuebler, Patrick Mueller
 * @see IAnalysisService
 */
public interface IAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig> extends BundleActivator {

    /**
     * Returns a Analysis Service Object which will be registered in OSGi
     *
     * @param bundleContext the current OSGi BundleContext
     * @return the corresponding analysisService
     * @throws IllegalStateException if an AnalysisService required by {@link IAnalysisService#getDependOnAnalyses()} is not registered in the context.
     * @see IAnalysisService#getDependOnAnalyses()
     */
    AbstractAnalysisService<Result, Config> getAnalysisService(BundleContext bundleContext) throws IllegalStateException;

}
