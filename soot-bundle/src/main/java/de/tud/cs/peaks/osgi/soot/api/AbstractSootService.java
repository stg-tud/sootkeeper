package de.tud.cs.peaks.osgi.soot.api;

import java.lang.instrument.IllegalClassFormatException;

import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;

public abstract class AbstractSootService extends AbstractAnalysisService<SootResult, SootConfig>{

	protected AbstractSootService(BundleContext context) throws IllegalClassFormatException {
		super(context);
	}

}
