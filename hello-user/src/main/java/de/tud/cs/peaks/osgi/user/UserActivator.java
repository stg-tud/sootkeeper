package de.tud.cs.peaks.osgi.user;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import de.tud.cs.peaks.osgi.hello.api.*;


public class UserActivator implements BundleActivator {
	BundleContext context;
	ServiceReference serviceReference;

	public void start(BundleContext context) throws Exception {
		this.context = context;
		System.out.println("Started Service User");
		serviceReference = context.getServiceReference(AbstractIntegerService.class.getName());
		AbstractIntegerService service = (AbstractIntegerService) context.getService(serviceReference);
		
		System.out.println("Use Service");
		service.performAnalysis(service.parseConfig(4));
		
	}

	public void stop(BundleContext context) throws Exception {
		this.context = context;
		
		context.ungetService(serviceReference);
		System.out.println("Stopped Service User");
	}

}
