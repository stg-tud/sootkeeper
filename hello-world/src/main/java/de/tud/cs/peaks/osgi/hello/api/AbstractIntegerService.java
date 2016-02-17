package de.tud.cs.peaks.osgi.hello.api;

import java.lang.instrument.IllegalClassFormatException;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;

public abstract class AbstractIntegerService extends AbstractAnalysisService<IntegerResult, IntegerConfig>{

	public AbstractIntegerService(BundleContext context) throws IllegalStateException, IllegalClassFormatException {
		super(context);
	}

	@Override
	public Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> getApiClass() {
		return AbstractIntegerService.class;
	}
}
