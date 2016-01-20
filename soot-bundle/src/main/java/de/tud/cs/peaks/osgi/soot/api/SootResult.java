package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

public class SootResult implements IAnalysisResult{
	int i;
	
	public SootResult(int i) {
		this.i = i;
	}
	
	@Override
	public String toString() {
		return "Result: " + i;
	}
}
