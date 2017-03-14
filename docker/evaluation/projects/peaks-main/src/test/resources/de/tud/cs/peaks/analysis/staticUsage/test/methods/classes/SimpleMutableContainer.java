package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes;

public class SimpleMutableContainer {
	private SimpleMutableClass mutableClass;

	public SimpleMutableContainer(SimpleMutableClass mutableClass){
		this.mutableClass =  mutableClass;
	}
	
	public SimpleMutableClass getMutableClass() {
		return mutableClass;
	}
}
