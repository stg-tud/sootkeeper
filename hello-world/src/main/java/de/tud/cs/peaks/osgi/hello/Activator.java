package de.tud.cs.peaks.osgi.hello;

import java.lang.instrument.IllegalClassFormatException;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.hello.api.AbstractIntegerService;
import de.tud.cs.peaks.osgi.hello.api.IntegerConfig;
import de.tud.cs.peaks.osgi.hello.api.IntegerResult;
import de.tud.cs.peaks.osgi.hello.impl.IntegerService;

/**
 * 
 * @author Florian Kuebler
 *
 */
public class Activator extends AbstractAnalysisActivator<IntegerResult, IntegerConfig> {

	@Override
	public AbstractAnalysisService<IntegerResult, IntegerConfig> getAnalysisService() throws IllegalStateException, IllegalClassFormatException {
		return new IntegerService(getBundleContext());
	}


}
