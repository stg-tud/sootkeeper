package de.tud.cs.peaks.osgi.framework.api;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

/**
 * An abstract {@link org.osgi.framework.BundleActivator} for the use of an {@link IAnalysisService}.
 * It handles the AnalysisService registration.
 *
 * @param <Result> The type of the {@link IAnalysisResult} the corresponding AnalysisService produces.
 * @param <Config> The type of the {@link IAnalysisConfig} the corresponding AnalysisService uses.
 * @author Florian Kuebler, Patrick Mueller
 */
public abstract class AbstractAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig>
        implements IAnalysisActivator<Result, Config> {

    /**
     * The context of this bundle.
     */
    private BundleContext context = null;

    /**
     * The registration of the AnalysisService that belongs to this Activator.
     */
    private ServiceRegistration reg = null;

    /**
     * {@inheritDoc}
     *
     * @throws IllegalClassFormatException when the concrete service does not have a @DependsOn annotation.
     * @throws IllegalStateException       if an AnalysisService required by the @DependsOn annotation is not registered in the context.
     * @see AbstractAnalysisService
     * @see AbstractAnalysisService#checkService()
     */
    @Override
    public void start(BundleContext context) throws IllegalClassFormatException, IllegalStateException {
        this.context = context;

        // TODO may want to catch exception here
        AbstractAnalysisService<Result, Config> analysisService = getAnalysisService();
        reg = context.registerService(analysisService.getApiClass().getName(), analysisService, null);
    }


    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException
     */
    @Override
    public void stop(BundleContext context) throws IllegalClassFormatException {
        this.context = context;
        reg.unregister();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BundleContext getBundleContext() {
        return this.context;
    }

}
