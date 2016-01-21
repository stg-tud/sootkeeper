package container;

import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
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
    }

    public void listAnalyses() {
        findAnalyses();
        System.out.println("Available analyses:");
        for (String analysis : analyses.keySet()) {
            System.out.println("\t" + analysis);
        }
    }

    public void runAnalysis(String analysis,
                            String param) {
        findAnalyses();
        String classNameOfService = analyses.get(analysis);
        if (classNameOfService == null) {
            System.out.println("Could not run: " + analysis);
            return;
        }
        ServiceReference serviceReference = context.getServiceReference(classNameOfService);
        IAnalysisService<IAnalysisResult, IAnalysisConfig> service =
                (IAnalysisService<IAnalysisResult, IAnalysisConfig>) context.getService(serviceReference);
        IAnalysisConfig iAnalysisConfig = service.parseConfig(param);
        Future<IAnalysisResult> r = service.performAnalysis(iAnalysisConfig);
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
                    String className = ((IAnalysisService<?, ?>) service).getApiName();
                    analyses.put(shortName, className);
                }
                context.ungetService(serviceReference);
            }
        } catch (InvalidSyntaxException e) {
            // should not happen since null is no invalid syntax
        }
    }

    public void la() {
        listAnalyses();
    }


    public void ra(String analysis, String param) {
        runAnalysis(analysis, param);
    }
}
