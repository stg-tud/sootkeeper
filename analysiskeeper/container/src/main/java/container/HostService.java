package container;

import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.*;
import org.osgi.framework.wiring.FrameworkWiring;

import java.util.*;
import java.util.concurrent.Future;


class HostService {

    private final BundleContext context;
    /**
     * This table maps the different Analysis names to their service representation
     */
    private final Map<String, String> analyses;
    private String[] previous;

    HostService(BundleContext context) {
        this.context = context;
        analyses = new HashMap<>();
    }

    /**
     * This lists the available Analyses
     * Is bound to the OSGi Commandline
     */
    public void listAnalyses() {
        findAnalyses();
        System.out.println("Available analyses:");
        for (String analysis : analyses.keySet()) {
            System.out.print('\t');
            System.out.println(analysis);
        }
    }

    /**
     * Queries the OSGi Context for registered analysis Services and saves them in a Table
     */
    private void findAnalyses() {
        analyses.clear();
        try {
            ServiceReference<?>[] serviceReferences = context.getAllServiceReferences(null, null);
            for (ServiceReference<?> serviceReference : serviceReferences) {
                Object service = context.getService(serviceReference);
                if (service instanceof IAnalysisService) {
                    String shortName = ((IAnalysisService<?, ?>) service).getName();
                    String className = service.getClass().getName();
                    analyses.put(shortName, className);
                }
                context.ungetService(serviceReference);
            }
        } catch (InvalidSyntaxException ignored) {
            // should not happen since null is no invalid syntax
        }
    }

    /**
     * Is bound to the OSGi Commandline
     * @see HostService#listAnalyses()
     */
    public void la() {
        listAnalyses();
    }

    /**
     * Is bound to the OSGi Commandline
     * @see HostService#runAnalysis(String...)
     */
    public void ra(String... params) {
        runAnalysis(params);
    }

    /**
     * Runs the given Analysis
     *
     * Is bound to the OSGi Commandline
     * @param params the first entry is the name of the analysis to run, the following entries are optional arguments to the analysis
     */
    public void runAnalysis(String... params) {
        previous = params.clone();
        findAnalyses();
        if (params.length < 1) {
            System.out.println("Please provide an analysis name.");
            return;
        }
        String analysis = params[0];
        String classNameOfService = analyses.get(analysis);
        if (classNameOfService == null) {
            System.out.println("Could not run: " + analysis);
            return;
        }
        ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceReference =
                (ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>>)
                        context.getServiceReference(classNameOfService);


        IAnalysisService<IAnalysisResult, IAnalysisConfig> service = context.getService(serviceReference);

        IAnalysisConfig iAnalysisConfig = service.parseConfig(Arrays.copyOfRange(params, 1, params.length));
        Future<IAnalysisResult> r = service.performAnalysis(iAnalysisConfig);
        context.ungetService(serviceReference);
    }

    /**
     * Repeats the previous Analysis with the same arguments, e.g. after reloading
     * Is bound to the OSGi Commandline
     */
    public void rep() {
        if (previous != null) {
            runAnalysis(previous);
        } else {
            System.out.println("Use real commands first.");
        }
    }

    /**
     * Forces the OSGi framework to reload the given Analysis from its jar, this is done recursively for all dependent analyses
     * Is bound to the OSGi Commandline
     * @param name the analysis to reload
     */
    public void updateAnalysis(String name) {
        try {
            findAnalyses();
            String classNameOfService = analyses.get(name);
            if (classNameOfService == null) {
                System.out.println("Could find: " + name);
                return;
            }
            ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceReference =
                    (ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>>)
                            context.getServiceReference(classNameOfService);
            IAnalysisService<IAnalysisResult, IAnalysisConfig> service = context.getService(serviceReference);
            service.getBundle().update();
            context.getBundle(0L).adapt(FrameworkWiring.class).refreshBundles(Collections.singleton(service.getBundle()));
        } catch (BundleException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @see HostService#updateAnalysis(String)
     */
    public void ua(String name) {
        updateAnalysis(name);
    }
}