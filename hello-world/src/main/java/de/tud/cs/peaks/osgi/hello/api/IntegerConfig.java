package de.tud.cs.peaks.osgi.hello.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;

public class IntegerConfig extends IAnalysisConfig {
	
	int i;
	
	public IntegerConfig(int i) {
		this.i = i;
	}
	
	public int getValue(){
		return i;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IntegerConfig that = (IntegerConfig) o;

		return i == that.i;

	}

	@Override
	public int hashCode() {
		return i;
	}
}
