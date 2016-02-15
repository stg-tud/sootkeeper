package de.tud.cs.peaks.osgi.soot;

import java.lang.instrument.IllegalClassFormatException;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.soot.api.SootBundleConfig;
import de.tud.cs.peaks.osgi.soot.api.SootBundleResult;
import de.tud.cs.peaks.osgi.soot.impl.SootService;

/**
 * 
 * @author Florian Kuebler
 *
 */
public class Activator extends AbstractAnalysisActivator<SootBundleResult, SootBundleConfig> {

	@Override
	public AbstractAnalysisService<SootBundleResult, SootBundleConfig> getAnalysisService() throws IllegalStateException, IllegalClassFormatException {
		return new SootService(getBundleContext());
	}


}
