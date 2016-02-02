package container;

import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;


public class HostService {

    private final BundleContext context;
    /**
     * This table maps the different Analysis names to their service representation
     */
    private final Map<String, String> analyses;

    public HostService(BundleContext context) {
        this.context = context;
        analyses = new HashMap<>();
    }

    public void listAnalyses() {
        findAnalyses();
        System.out.println("Available analyses:");
        for (String analysis : analyses.keySet()) {
            System.out.print('\t');
            System.out.println(analysis);
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
        ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>> serviceReference =
                (ServiceReference<IAnalysisService<IAnalysisResult, IAnalysisConfig>>)
                        context.getServiceReference(classNameOfService);


        IAnalysisService<IAnalysisResult, IAnalysisConfig> service = context.getService(serviceReference);

        IAnalysisConfig iAnalysisConfig = service.parseConfig(param);
        Future<IAnalysisResult> r = service.performAnalysis(iAnalysisConfig);
        context.ungetService(serviceReference);
    }


    private void findAnalyses() {
        analyses.clear();
        try {
            ServiceReference<?>[] serviceReferences = context.getAllServiceReferences(null, null);
            for (ServiceReference<?> serviceReference : serviceReferences) {
                Object service = context.getService(serviceReference);
                if (service instanceof IAnalysisService) {
                    String shortName = ((IAnalysisService<?, ?>) service).getName();
                    String className = ((IAnalysisService<?, ?>) service).getApiName();
                    analyses.put(shortName, className);
                }
                context.ungetService(serviceReference);
            }
        } catch (InvalidSyntaxException ignored) {
            // should not happen since null is no invalid syntax
        }
    }

    public void la() {
        listAnalyses();
    }


    public void ra(String analysis, String param) {
        runAnalysis(analysis, param);
    }

    public void ra(String analysis) {
        runAnalysis(analysis, "");
    }

    public void runAnalysis(String analysis) {
        runAnalysis(analysis, "");
    }
}
