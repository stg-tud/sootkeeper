package de.tud.cs.peaks.osgi.framework.api;

import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.instrument.IllegalClassFormatException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * @param <Result>
 * @param <Config>
 * @author Florian Kuebler
 */
public abstract class AbstractAnalysisService<Result extends IAnalysisResult, Config extends IAnalysisConfig>
        implements IAnalysisService<Result, Config> {

    /**
     *
     */
    private static final ExecutorService POOL = new ForkJoinPool();

    /**
     *
     */
    private final Map<IAnalysisConfig, Future<Result>> results;

    /**
     *
     */
    private BundleContext context;

    /**
     * @param context
     * @throws IllegalClassFormatException
     * @throws IllegalStateException
     */
    protected AbstractAnalysisService(BundleContext context) throws IllegalClassFormatException {

        checkService();

        this.results = new ConcurrentHashMap<>();
        this.context = context;
    }

    /**
     * @return
     */
    public BundleContext getBundleContext() {
        return this.context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Future<Result> performAnalysis(final Config config) {

        if (results.containsKey(config)) {
            return results.get(config);
        }

        System.out.println("Submitting task: " + config);
        Future<Result> result = POOL.submit(new Callable<Result>() {

            @Override
            public Result call() throws Exception {

                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> results = new HashMap<>();
                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> futureResults = new HashMap<>();

                DependsOn annotation = AbstractAnalysisService.this.getClass().getAnnotation(DependsOn.class);

                for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysisClass : annotation
                        .value()) {
                    AbstractAnalysisService<IAnalysisResult, IAnalysisConfig> analysis = getServiceInstance(analysisClass);

                    IAnalysisConfig analysisConfig = convertConfig(config, analysisClass);

                    Future<IAnalysisResult> analysisResult = analysis.performAnalysis(analysisConfig);

                    futureResults.put(analysisClass, analysisResult);
                }

                for (Entry<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> entry : futureResults
                        .entrySet()) {
                    results.put(entry.getKey(), entry.getValue().get());
                }

                return runAnalysis(config, results);
            }
        });

        results.put(config, result);

        return result;
    }

    /**
     * @param serviceClass
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    protected synchronized <R extends IAnalysisResult, C extends IAnalysisConfig> AbstractAnalysisService<R, C> getServiceInstance(
            Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {

        if (context != null) {
            ServiceReference ref = context.getServiceReference(serviceClass.getName());
            if (ref != null) {
                Object service = context.getService(ref);
                if (service instanceof AbstractAnalysisService<?, ?>) {
                    return (AbstractAnalysisService<R, C>) context.getService(ref);
                } else {
                    throw new IllegalArgumentException(serviceClass.getName() + " is no IAnalysisService");
                }
            }
        }

        throw new IllegalArgumentException(serviceClass.getName() + " is not a registered service");
    }

    /**
     *
     */
    private void checkService() throws IllegalClassFormatException {

        // Get annotation for required analyses
        DependsOn annotation = this.getClass().getAnnotation(DependsOn.class);
        if (annotation == null) {
            throw new IllegalClassFormatException(this.getClass().getName() + " has no @DependsOn annotation");
        }

        // check whether all required analyses are available
        for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysis : annotation.value()) {
            ServiceReference ref = context.getServiceReference(analysis.getName());
            if (ref == null) {
                throw new IllegalStateException("Required AnalysisService " + analysis.getName() + " is not registered");
            }
        }
    }

    @Override
    public String getApiName() {
        return getClass().getCanonicalName();
    }
}
