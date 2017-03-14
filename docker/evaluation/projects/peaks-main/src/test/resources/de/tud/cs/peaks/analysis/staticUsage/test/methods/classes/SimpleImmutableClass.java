package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes;

public class SimpleImmutableClass {
	private String info;
	
	public SimpleImmutableClass(String info){
		this.info = info;
	}
	
	public String getInfo(){
		return this.info;
	}
}
