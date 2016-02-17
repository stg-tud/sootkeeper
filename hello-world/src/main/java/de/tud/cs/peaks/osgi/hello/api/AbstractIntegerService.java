package de.tud.cs.peaks.osgi.hello.api;

import java.lang.instrument.IllegalClassFormatException;

import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;

public abstract class AbstractIntegerService extends AbstractAnalysisService<IntegerResult, IntegerConfig>{

	public AbstractIntegerService(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
		super(context);
	}

	@Override
	public String getApiName() {
		return AbstractIntegerService.class.getName();
	}
}
