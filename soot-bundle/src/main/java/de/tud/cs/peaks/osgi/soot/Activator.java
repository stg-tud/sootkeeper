package de.tud.cs.peaks.osgi.soot;

import java.lang.instrument.IllegalClassFormatException;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisActivator;
import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;
import de.tud.cs.peaks.osgi.framework.api.IAnalysisService;
import de.tud.cs.peaks.osgi.soot.api.AbstractSootService;
import de.tud.cs.peaks.osgi.soot.api.SootConfig;
import de.tud.cs.peaks.osgi.soot.api.SootResult;
import de.tud.cs.peaks.osgi.soot.impl.SootService;

/**
 * 
 * @author Florian Kuebler
 *
 */
public class Activator extends AbstractAnalysisActivator<SootResult, SootConfig> {

	@Override
	public AbstractAnalysisService<SootResult, SootConfig> getAnalysisService() throws IllegalStateException, IllegalClassFormatException {
		return new SootService(getBundleContext());
	}


}
