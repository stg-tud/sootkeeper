package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.sootconfig.SootResult;

public class SootBundleResult implements IAnalysisResult{
	private final SootResult sootResult;

	public SootBundleResult(SootResult sootResult) {
		this.sootResult = sootResult;
	}

	@Override
	public String toString() {
		return sootResult.toString();
	}
}
