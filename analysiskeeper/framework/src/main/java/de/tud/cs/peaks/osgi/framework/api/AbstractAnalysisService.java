package de.tud.cs.peaks.osgi.framework.api;

import com.google.common.base.Stopwatch;
import de.tud.cs.peaks.osgi.framework.api.annotations.DependsOn;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.*;
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

    private boolean checked = false;

    /**
     * Constructor of the AnalysisService.
     *
     * @param context the context of the bundle this service belongs to.
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
    public final Future<Result> performAnalysis(final Config config) {
        checkService();
        final Stopwatch overall = Stopwatch.createStarted();

        if (results.containsKey(config)) {
            overall.stop();
            logTime(overall);
            System.out.println(getName() + " has run for " + overall.elapsed(TimeUnit.MILLISECONDS) + " ms. The result was cached.");
            return results.get(config);
        }

        System.out.println("In " + getName() + " Submitting task: " + config);
        Future<Result> result = POOL.submit(() -> {

            Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, IAnalysisResult> results1 = new HashMap<>();
            Map<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> futureResults = new HashMap<>();

            for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysisClass : getDependOnAnalyses()) {
                IAnalysisService<IAnalysisResult, IAnalysisConfig> analysis = getServiceInstance(analysisClass);

                IAnalysisConfig analysisConfig = convertConfig(config, analysisClass);

                Future<IAnalysisResult> analysisResult = analysis.performAnalysis(analysisConfig);

                futureResults.put(analysisClass, analysisResult);
            }

            for (Map.Entry<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>, Future<IAnalysisResult>> entry : futureResults
                    .entrySet()) {
                results1.put(entry.getKey(), entry.getValue().get());
                ungetService(entry.getKey());
            }
            Stopwatch analysisWatch = Stopwatch.createStarted();
            Result result1 = runAnalysis(config, results1);
            overall.stop();
            analysisWatch.stop();
            logTime(analysisWatch);
            System.out.println(getName() + " has run for " + overall.elapsed(TimeUnit.MILLISECONDS) + " ms, " + analysisWatch.elapsed(TimeUnit.MILLISECONDS) + " ms");
            return result1;
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


    @Override
    public boolean shouldBeHidden() {
        return false;
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
    private synchronized <R extends IAnalysisResult, C extends IAnalysisConfig> AbstractAnalysisService<R, C> getServiceInstance(
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
     * @throws IllegalStateException       if an AnalysisService required by the @DependsOn annotation is not registered in the context.
     */
    private void checkService() throws  IllegalStateException {
        if (checked) {
            return;
        }
        // check whether all required analyses are available
        for (Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> analysis : getDependOnAnalyses()) {
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
    protected abstract List<Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>>> getDependOnAnalyses();

    private void logTime(Stopwatch time) {
        File f = new File("timings.txt");
        try (FileWriter fw = new FileWriter(f, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(getName() + "," + time.elapsed(TimeUnit.SECONDS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
