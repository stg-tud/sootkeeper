package container;

import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.Hashtable;
import java.util.concurrent.Future;

public class HostService {

    private final BundleContext context;
    /**
     * This table maps the different Analysis names to their service representation
     */
    private final Hashtable<String, String> analyses;

    public HostService(BundleContext context) {
        this.context = context;
        this.analyses = new Hashtable<>();
        findAnalyses();
    }

    @Descriptor("Lists all available analyses")
    public void listAnalyses() {
        findAnalyses();
        System.out.println("Available analyses:");
        for (String analysis : analyses.keySet()) {
            System.out.println("\t" + analysis);
        }
    }

    @Descriptor("Run analysis")
    public void runAnalysis(@Descriptor("The analysis to run") String analysis,
                            @Descriptor("Parameters for the analysis") String param) {
        findAnalyses();
        String classNameOfService = analyses.get(analysis);
        if (classNameOfService == null) {
            System.out.println("Could not run: " + analysis);
            return;
        }
        ServiceReference serviceReference = context.getServiceReference(classNameOfService);
        IAnalysisService<IAnalysisResult, IAnalysisConfig> service =
                (IAnalysisService<IAnalysisResult, IAnalysisConfig>) context.getService(serviceReference);
        Future<IAnalysisResult> r = service.performAnalysis(service.parseConfig(param));
        context.ungetService(serviceReference);
    }


    private void findAnalyses() {
        analyses.clear();
        try {
            ServiceReference[] serviceReferences = context.getAllServiceReferences(null, null);
            for (ServiceReference serviceReference : serviceReferences) {
                Object service = context.getService(serviceReference);
                if (service instanceof IAnalysisService) {
                    String shortName = ((IAnalysisService<?, ?>) service).getName();
                    String className = serviceReference.toString();
                    className = className.substring(1, className.length() - 1);
                    analyses.put(shortName, className);
                }
                context.ungetService(serviceReference);
            }
        } catch (InvalidSyntaxException e) {
            // should not happen since null is no invalid syntax
        }
    }

    @Descriptor("Lists all available analyses")
    public void la() {
        listAnalyses();
    }

    @Descriptor("Run analysis")
    public void ra(@Descriptor("The analysis to run") String analysis, @Descriptor("Parameters for the analysis") String param) {
        runAnalysis(analysis, param);
    }
}
