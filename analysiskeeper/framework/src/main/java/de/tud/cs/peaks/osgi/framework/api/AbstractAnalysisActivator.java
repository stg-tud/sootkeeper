package de.tud.cs.peaks.osgi.framework.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.lang.instrument.IllegalClassFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
     * The registration of the AnalysisService that belongs to this Activator.
     */
    private List<ServiceRegistration> registrations = new LinkedList<>();

    /**
     * {@inheritDoc}
     *
     * @see AbstractAnalysisService
     * @see AbstractAnalysisService#checkService()
     */
    @Override
    public void start(BundleContext context) throws IllegalClassFormatException, IllegalStateException {
        List<AbstractAnalysisService<Result, Config>> analysisServices = getAnalysisServices(context);
        registrations.addAll(analysisServices.stream().map(analysisService -> context.registerService(analysisService.getClass().getName(), analysisService, null)).collect(Collectors.toList()));
    }


    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException
     */
    @Override
    public void stop(BundleContext context) throws IllegalClassFormatException {
        registrations.forEach(ServiceRegistration::unregister);
    }


}
