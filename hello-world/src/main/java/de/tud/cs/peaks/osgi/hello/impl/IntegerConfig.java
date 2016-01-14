package de.tud.cs.peaks.osgi.hello.impl;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;

public class IntegerConfig implements IAnalysisConfig {
	
	int i;
	
	public IntegerConfig(int i) {
		this.i = i;
	}
	
	public int getValue(){
		return i;
	}
}
