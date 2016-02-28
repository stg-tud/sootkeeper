package de.tud.cs.peaks.osgi.framework.api;

import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.lang.instrument.IllegalClassFormatException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    private final BundleContext context;
    private List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> dependOnAnalyses;


    /**
     * @param context
     * @throws IllegalClassFormatException
     * @throws IllegalStateException
     */
    protected AbstractAnalysisService(BundleContext context) throws IllegalClassFormatException {
        this.results = new ConcurrentHashMap<>();
        this.context = context;

        checkService();
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
            public Result call() throws InterruptedException, ExecutionException {

                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> results = new HashMap<>();
                Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> futureResults = new HashMap<>();

                for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysisClass : dependOnAnalyses) {
                    AbstractAnalysisService<IAnalysisResult, IAnalysisConfig> analysis = getServiceInstance(analysisClass);

                    IAnalysisConfig analysisConfig = convertConfig(config, analysisClass);

                    Future<IAnalysisResult> analysisResult = analysis.performAnalysis(analysisConfig);

                    futureResults.put(analysisClass, analysisResult);
                }

                for (Map.Entry<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> entry : futureResults
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
        DependsOn annotation = getClass().getAnnotation(DependsOn.class);
        if (annotation == null) {
            throw new IllegalClassFormatException(getClass().getName() + " has no @DependsOn annotation");
        }
        dependOnAnalyses = Arrays.asList(annotation.value());
        // check whether all required analyses are available
        for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysis : dependOnAnalyses) {
            ServiceReference ref = context.getServiceReference(analysis.getName());
            if (ref == null) {
                throw new IllegalStateException("Required AnalysisService " + analysis.getName() + " is not registered");
            }
        }
    }

    @Override
    public List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses() {
        return Collections.unmodifiableList(dependOnAnalyses);
    }

    @Override
    public Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> getApiClass() {
        return (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>) this.getClass();
    }

    @Override
    public void clearCache() {
        System.out.println("Clearing cache of: " + getName());
        results.clear();
        try {
            Arrays.stream(context.getServiceReferences((String) null, null))
                    .map(context::getService)
                    .filter(s -> s instanceof IAnalysisService)
                    .map(s -> ((IAnalysisService<?, ?>) s))
                    .filter(this::containsDependingAnalyses)
                    .forEach(IAnalysisService::clearCache);
            // Its some fucking classloader thing Don't forget  !!!
        } catch (InvalidSyntaxException ignored) {
            // Will not happen
        }
    }

    private boolean containsDependingAnalyses(IAnalysisService<?, ?> service) {
        return service.getDependOnAnalyses()
                .stream()
                .map(this::getServiceInstanceOrNull)
                .filter(s -> s != null).collect(Collectors.toList())
                .contains(getServiceInstance((Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>) getClass()));
    }

    private <R extends IAnalysisResult, C extends IAnalysisConfig> AbstractAnalysisService<R, C> getServiceInstanceOrNull(
            Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> serviceClass) {
        try {
            return getServiceInstance(serviceClass);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }


}
