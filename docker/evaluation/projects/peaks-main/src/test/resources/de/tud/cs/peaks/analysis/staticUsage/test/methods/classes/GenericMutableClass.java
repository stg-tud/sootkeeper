package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes;

public class GenericMutableClass<A> {
	private A info;
	
	public A getInfo(){
		return info;
	}
	
	public void setInfo(A info){
		this.info = info;
	}
}
