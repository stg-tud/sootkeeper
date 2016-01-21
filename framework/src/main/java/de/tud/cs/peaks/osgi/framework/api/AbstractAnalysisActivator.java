package de.tud.cs.peaks.osgi.framework.api;

import java.lang.instrument.IllegalClassFormatException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

/**
 * 
 * @author Florian Kuebler
 *
 * @param <Result>
 * @param <Config>
 */
public abstract class AbstractAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig>
		implements IAnalysisActivator<Result, Config> {

	/**
	 * 
	 */
	private BundleContext context = null;
	
	/**
	 * 
	 */
	private ServiceRegistration reg = null;

	/**
	 * {@inheritDoc}
	 * @throws IllegalClassFormatException 
	 * @throws IllegalStateException 
	 */
	@Override
	public void start(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
		this.context = context;

		// TODO may want to catch exception here
		this.reg = context.registerService(getAnalysisService().getApiName(), getAnalysisService(), null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		this.context = context;		
		this.reg.unregister();
		
		//TODO stop service
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public BundleContext getBundleContext(){
		return this.context;
	}

}
