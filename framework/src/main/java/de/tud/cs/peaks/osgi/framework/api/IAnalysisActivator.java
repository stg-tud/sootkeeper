package de.tud.cs.peaks.osgi.framework.api;

import java.lang.instrument.IllegalClassFormatException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

/**
 * 
 * @author Florian Kuebler
 *
 * @param <Result>
 * @param <Config>
 */
public interface IAnalysisActivator<Result extends IAnalysisResult, Config extends IAnalysisConfig> extends BundleActivator{

	/**
	 * 
	 * @return
	 * @throws IllegalClassFormatException 
	 * @throws IllegalStateException 
	 */
	AbstractAnalysisService<Result, Config> getAnalysisService() throws IllegalStateException, IllegalClassFormatException;

	
	/**
	 * 
	 * @return
	 */
	BundleContext getBundleContext();
}
