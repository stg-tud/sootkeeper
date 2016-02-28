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
 * @param <Result>
 * @param <Config>
 * @author Florian Kuebler
 */
public abstract class AbstractAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig>
        implements IAnalysisActivator<Result, Config> {

    /**
     *
     */
    private BundleContext context = null;

    /**
     *
     */
    private ServiceRegistration reg = null;

    /**
     * {@inheritDoc}
     *
     * @throws IllegalClassFormatException
     * @throws IllegalStateException
     */
    @Override
    public void start(BundleContext context) throws IllegalClassFormatException {
        this.context = context;

        // TODO may want to catch exception here
        AbstractAnalysisService<Result, Config> analysisService = getAnalysisService();
        this.reg = context.registerService(analysisService.getApiClass().getName(), analysisService, null);
        System.out.printf("");
    }


    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException
     */
    @Override
    public void stop(BundleContext context) throws IllegalClassFormatException {
        this.context = context;
        ((IAnalysisService<?, ?>) context.getService(reg.getReference())).clearCache();
        this.reg.unregister();

        //TODO stop service
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BundleContext getBundleContext() {
        return this.context;
    }

}
