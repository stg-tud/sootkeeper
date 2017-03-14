package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes;

public class GenericMutableContainer<A> {
	private GenericMutableClass<A> mutableClass;

	public GenericMutableContainer(GenericMutableClass<A> mutableClass){
		this.mutableClass =  mutableClass;
	}
	
	public GenericMutableClass<A> getMutableClass() {
		return mutableClass;
	}
	
	
}
