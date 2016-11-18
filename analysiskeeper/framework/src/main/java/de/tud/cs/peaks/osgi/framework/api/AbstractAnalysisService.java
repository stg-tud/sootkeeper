package de.tud.cs.peaks.osgi.framework.api;

import com.google.common.base.Stopwatch;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.instrument.IllegalClassFormatException;
import java.util.*;
import java.util.concurrent.*;

/**
 * An abstract class for AnalysisServices. Concrete implementations MUST declare an {@link DependsOn} annotation indicating on which analyses the service depends on.
 * This class provides automatic methods to run depending analyses and to cache the results.
 *
 * @param <Result> The type of the {@link IAnalysisResult} this AnalysisService produces.
 * @param <Config> The type of the {@link IAnalysisConfig} this AnalysisService uses.
 * @author Florian Kuebler, Patrick Mueller
 */
public abstract class AbstractAnalysisService<Result extends IAnalysisResult, Config extends IAnalysisConfig>
        implements IAnalysisService<Result, Config> {

    /**
     * The Executor Service used to run analyses this service depends on concurrently.
     */
    private static final ExecutorService POOL = new ForkJoinPool();

    /**
     * A storage of (not necessary complete) results computed by this analysis for a given config.
     */
    private final Map<IAnalysisConfig, Future<Result>> results;

    /**
     * The context of this Bundle.
     */
    private final BundleContext context;

    /**
     * The Bundle which this AnalysisService belongs to.
     */
    private final Bundle bundle;

    /**
     * The list of all AnalysisServices this service depends on.
     */
    private List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> dependOnAnalyses;

    private boolean checked = false;


    /**
     * Constructor of the AnalysisService. Also checks the concrete service class layout ({@link AbstractAnalysisService#checkService()}).
     *
     * @param context the context of the bundle this service belongs to.
     * @throws IllegalClassFormatException when the concrete service does not have a @DependsOn annotation.
     * @throws IllegalStateException       if an AnalysisService required by the @DependsOn annotation is not registered in the context.
     * @see AbstractAnalysisService#checkService()
     */
    protected AbstractAnalysisService(BundleContext context) {
        this.results = new ConcurrentHashMap<>();
        this.context = context;
        this.bundle = context.getBundle();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Future<Result> performAnalysis(final Config config) throws IllegalClassFormatException {
        checkService();
        final Stopwatch overall = Stopwatch.createStarted();

        if (results.containsKey(config)) {
            overall.stop();
            System.out.println("Result cached: " + config + " (" + overall.toString() + ")");
            return results.get(config);
        }

        System.out.println("Submitting task: " + config);
        Future<Result> result = POOL.submit(new Callable<Result>() {

            @Override
            public Result call() throws InterruptedException, ExecutionException, IllegalClassFormatException {

                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> results = new HashMap<>();
                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> futureResults = new HashMap<>();

                for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysisClass : dependOnAnalyses) {
                    IAnalysisService<IAnalysisResult, IAnalysisConfig> analysis = getServiceInstance(analysisClass);

                    IAnalysisConfig analysisConfig = convertConfig(config, analysisClass);

                    Future<IAnalysisResult> analysisResult = analysis.performAnalysis(analysisConfig);

                    futureResults.put(analysisClass, analysisResult);
                }

                for (Map.Entry<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> entry : futureResults
                        .entrySet()) {
                    results.put(entry.getKey(), entry.getValue().get());
                    ungetService(entry.getKey());
                }
                Stopwatch analysisWatch = Stopwatch.createStarted();
                Result result = runAnalysis(config, results);
                overall.stop();
                analysisWatch.stop();
                System.out.println(getName() + " has run for " + overall + ", " + analysisWatch);
                return result;
            }
        });

        results.put(config, result);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bundle getBundle() {
        return bundle;
    }

    /**
     * Retrieves the AnalysisService instance if the given class from the bundle context ({@link AbstractAnalysisService#context}).
     * If the there is no service registered that belongs to the given class,
     * or the service is no {@link AbstractAnalysisService} an {@link IllegalArgumentException} is thrown.
     * The Service loaded should be released after use with {@link AbstractAnalysisService#ungetService(Class)};
     *
     * @param serviceClass the class of the service to load.
     * @param <R>          The result type of the analysis to load.
     * @param <C>          The config type of the analysis to load.
     * @return the AnalysisService registered for the given class.
     * @throws IllegalArgumentException if no service is registered under the given class or the service is no {@link AbstractAnalysisService}.
     */
    @SuppressWarnings("unchecked")
    protected synchronized <R extends IAnalysisResult, C extends IAnalysisConfig> AbstractAnalysisService<R, C> getServiceInstance(
            Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) throws IllegalArgumentException {
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
     * Calls {@link BundleContext#ungetService(ServiceReference)} for the AnalysisService of the given class.
     *
     * @param serviceClass the class of the service to unget.
     * @throws IllegalArgumentException if no service is registered under the given class or the service is no {@link AbstractAnalysisService}.
     */
    private synchronized void ungetService(
            Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) throws IllegalArgumentException {
        if (context != null) {
            ServiceReference ref = context.getServiceReference(serviceClass.getName());
            if (ref != null) {
                Object service = context.getService(ref);
                if (service instanceof AbstractAnalysisService<?, ?>) {
                    context.ungetService(ref);
                    return;
                } else {
                    throw new IllegalArgumentException(serviceClass.getName() + " is no IAnalysisService");
                }
            }
        }
        throw new IllegalArgumentException(serviceClass.getName() + " is not a registered service");
    }


    /**
     * Checks the the class layout of the concrete AnalysisService.
     *
     * @throws IllegalClassFormatException when the concrete Service does not have a @DependsOn annotation.
     * @throws IllegalStateException       if an AnalysisService required by the @DependsOn annotation is not registered in the context.
     */
    private void checkService() throws IllegalClassFormatException, IllegalStateException {
        if (checked) {
            return;
        }
        // Get annotation for required analyses
        DependsOn annotation = getClass().getAnnotation(DependsOn.class);
        if (annotation == null) {
            throw new IllegalClassFormatException(getClass().getName() + " has no @DependsOn annotation");
        }
        dependOnAnalyses = Collections.unmodifiableList(Arrays.asList(annotation.value()));
        // check whether all required analyses are available
        for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysis : dependOnAnalyses) {
            ServiceReference ref = context.getServiceReference(analysis.getName());
            if (ref == null) {
                throw new IllegalStateException("Required AnalysisService " + analysis.getName() + " is not registered");
            }
        }
        checked = true;
    }

    /**
     * @return the list of all AnalysisServices this service depends on.
     */
    public List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return dependOnAnalyses;
    }
}
