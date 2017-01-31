package container;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.knopflerfish.service.console.CommandGroup.GROUP_NAME;

/**
 * This is the BundleActivator for the AnalysisKeeper Container
 * It registers the commandline commands and the service that handles them
 * As an Analysis Author You should not need to change stuff here
 */
public class HostActivator implements BundleActivator {
    /*
     * String Constants used to register Commands
     */
    private static final String OSGI_COMMAND_SCOPE = "osgi.command.scope";
    private static final String OSGI_COMMAND_FUNCTION = "osgi.command.function";
    private static final String SCOPE = "sootkeeper";
    private static final String[] COMMAND_FUNCTIONS = {"listAnalyses", "la", "ra", "runAnalysis", "rep", "ua", "updateAnalysis"};

    private ServiceRegistration<?> registration;

    @SuppressWarnings("UseOfObsoleteCollectionType")
    /**
     * generates The HashTable that is used to register the Commands
     */
    private static Dictionary<String, Object> generateProperties() {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put(OSGI_COMMAND_SCOPE, SCOPE);
        properties.put(OSGI_COMMAND_FUNCTION, COMMAND_FUNCTIONS);
        properties.put(GROUP_NAME, HostService.SOOTKEEPER_NAME);
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        HostService s = new HostService(context);
        Dictionary<String, Object> properties = generateProperties();
        registration = context.registerService("container.HostService", s, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }

}
