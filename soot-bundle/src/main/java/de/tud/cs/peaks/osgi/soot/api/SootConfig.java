package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;

public class SootConfig implements IAnalysisConfig {
	
	int i;
	
	public SootConfig(int i) {
		this.i = i;
	}
	
	public int getValue(){
		return i;
	}
}
