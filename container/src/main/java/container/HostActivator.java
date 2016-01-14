package container;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;

public class HostActivator implements BundleActivator {

    private ServiceRegistration registration;

    public void start(BundleContext context) throws Exception {
        HostService s = new HostService(context);
        Dictionary<String,Object > properties = generateProperties();
        registration = context.registerService("container.HostService", s, properties);
    }

    private static Hashtable<String, Object> generateProperties() {
        Hashtable<String,Object > properties = new Hashtable<>();
        properties.put("osgi.command.scope", "sootkeeper");
        properties.put("osgi.command.function", new String[]{"listAnalyses","la","ra","runAnalyses"});
        return properties;
    }

    public void stop(BundleContext context) throws Exception {
        registration.unregister();
    }

}
