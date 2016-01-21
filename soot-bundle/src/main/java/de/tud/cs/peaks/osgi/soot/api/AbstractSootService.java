package de.tud.cs.peaks.osgi.soot.api;

import java.lang.instrument.IllegalClassFormatException;

import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;

public abstract class AbstractSootService extends AbstractAnalysisService<SootResult, SootConfig>{

	public AbstractSootService(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
		super(context);
	}

	public String getApiName(){
		return AbstractSootService.class.getCanonicalName();
	}

}
