package de.tud.cs.peaks.osgi.hello.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;

public class IntegerResult implements IAnalysisResult{
	int i;
	
	public IntegerResult(int i) {
		this.i = i;
	}
	
	@Override
	public String toString() {
		return "Result: " + i;
	}
}
