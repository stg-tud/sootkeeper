package de.tud.cs.peaks.osgi.soot.api;

import java.lang.instrument.IllegalClassFormatException;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import org.osgi.framework.BundleContext;

import de.tud.cs.peaks.osgi.framework.api.AbstractAnalysisService;

public abstract class AbstractSootService extends AbstractAnalysisService<SootBundleResult, SootBundleConfig>{

	protected AbstractSootService(BundleContext context) throws IllegalClassFormatException {
		super(context);
	}

	@Override
	public Class<? extends AbstractAnalysisService<? extends IAnalysisResult, ? extends IAnalysisConfig>> getApiClass() {
		return AbstractSootService.class;
	}
}
