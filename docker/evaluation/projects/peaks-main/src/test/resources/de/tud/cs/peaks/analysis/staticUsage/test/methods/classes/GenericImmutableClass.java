package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes;

public class GenericImmutableClass<A> {
	private A info;
	
	public GenericImmutableClass(A info){
		this.info = info;
	}
	
	public A getInfo(){
		return info;
	}	
}
