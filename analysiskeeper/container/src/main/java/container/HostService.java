package container;

import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.apache.felix.service.command.Descriptor;
import org.knopflerfish.service.console.CommandGroupAdapter;
import org.knopflerfish.service.console.Session;
import org.osgi.framework.*;
import org.osgi.framework.wiring.FrameworkWiring;

import java.io.PrintWriter;
import java.io.Reader;
import java.lang.instrument.IllegalClassFormatException;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Provide commands for the OSGi shell
 * @author Florian Kuebler, Patrick Mueller
 */
class HostService extends CommandGroupAdapter {

    public final static String USAGE_SOOTKEEPER = "";
    public final static String[] HELP_SOOTKEEPER = new String[]{""};
    public static final String SOOTKEEPER_NAME = "sootkeeper";

    private final BundleContext context;
    /**
     * This table maps the different Analysis names to their service representation
     */
    private final Map<String, String> analyses;
    private final Map<String, String> hiddenAnalyses;
    private String[] previous;

    HostService(BundleContext context) {
        super(SOOTKEEPER_NAME, "");
        this.context = context;
        analyses = new HashMap<>();
        hiddenAnalyses = new HashMap<>();
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
                    if (!((IAnalysisService) service).shouldBeHidden()) {
                        analyses.put(shortName, className);
                    } else {
                        hiddenAnalyses.put(shortName, className);
                    }
                }
                context.ungetService(serviceReference);
            }
        } catch (InvalidSyntaxException ignored) {
            // should not happen since null cannot be invalid syntax
        }
    }

    /**
     * Is bound to the OSGi Commandline
     *
     * @see HostService#listAnalyses()
     */
    public void la() {
        listAnalyses();
    }

    /**
     * Is bound to the OSGi Commandline
     *
     * @see HostService#runAnalysis(String...)
     */
    public void ra(String... params) throws IllegalClassFormatException {
        runAnalysis(params);
    }

    /**
     * Runs the given Analysis
     * <p>
     * Is bound to the OSGi Commandline
     *
     * @param params the first entry is the name of the analysis to run, the following entries are optional arguments to the analysis
     */
    @Descriptor("Runs an analysis")
    public void runAnalysis(String... params) throws IllegalClassFormatException {
        if (params.length < 1) {
            System.out.println("Please provide an analysis name.");
            return;
        }
        previous = params.clone();
        findAnalyses();
        String analysis = params[0];
        String classNameOfService = analyses.get(analysis);
        if (classNameOfService == null) {
            classNameOfService = hiddenAnalyses.get(analysis);
        }
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
    public void rep() throws IllegalClassFormatException {
        if (previous != null) {
            runAnalysis(previous);
        } else {
            System.out.println("Use real commands first.");
        }
    }

    /**
     * Forces the OSGi framework to reload the given Analysis from its jar, this is done recursively for all dependent analyses
     * Is bound to the OSGi Commandline
     *
     * @param name the analysis to reload
     */
    public void updateAnalysis(String name) {
        try {
            findAnalyses();
            System.out.println("Updating " + name);
            String classNameOfService = analyses.get(name);
            if (classNameOfService == null) {
                classNameOfService = hiddenAnalyses.get(name);
            }
            if (classNameOfService == null) {
                System.out.println("Could not find: " + name);
                return;
            }
            ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceReference =
                    (ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>>)
                            context.getServiceReference(classNameOfService);
            IAnalysisService<IAnalysisResult, IAnalysisConfig> service = context.getService(serviceReference);
            service.getBundle().update();
            context.getBundle(0L).adapt(FrameworkWiring.class)
                    .refreshBundles(Collections.singleton(service.getBundle()), new UpdateListener(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HostService#updateAnalysis(String)
     */
    public void ua(String name) {
        updateAnalysis(name);
    }

    public void ua() {
        updateAnalysis();
    }

    public void updateAnalysis() {
        if (previous != null) {
            updateAnalysis(previous[0]);
        } else {
            System.out.println("This only works when an analysis was run previously, please provide a name");
        }
    }

    public int cmdSOOTKEEPER(Dictionary opts, Reader in, PrintWriter Out, Session session) {
        return 0;
    }

    private class UpdateListener implements FrameworkListener {
        private final String analysis;

        private UpdateListener(String analysis) {
            this.analysis = analysis;
        }

        @Override
        public void frameworkEvent(FrameworkEvent event) {
            System.out.println("Updated " + analysis);
        }
    }
}
