package de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.simple;

import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.GenericImmutableClass;
import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.GenericMutableClass;
import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.GenericMutableContainer;
import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.SimpleImmutableClass;
import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.SimpleMutableClass;
import de.tud.cs.peaks.analysis.staticUsage.test.methods.classes.SimpleMutableContainer;

public class SimpleSideEffects {
	private static SimpleImmutableClass a;
	private static SimpleMutableClass b;
	private static SimpleMutableContainer c;
	
	
	/*
	 * The following methods should be analyzed as side effect free.
	 */
	
	public static void noSideEffectsAndReturn(){
		System.out.println("Is system out a sideeffect we should care about?");
		return;
	}
	
	public static int purelyFunctionalComputition(){
		return 40 + 2;
	}
	
	public static int purelyFunctionalComputitionWithInput(int a, int b){
		return a*b;
	}
	
	public static int transitivePurelyFunctionlComputition(){
		int a = purelyFunctionalComputition();
		return purelyFunctionalComputitionWithInput(a, a);
	}
	
	
	
	/*
	 * The following methods have side effects and should be analyzed respectively. Or shouldn't they?
	 */
	
	public static void modifyInput(GenericMutableClass<Integer> x, Integer value){
		x.setInfo(value);
	}
	
	public static void modifyInputWithPrivateInfo(SimpleMutableClass x){
		x.setInfo(a.getInfo());
	}
	
	public static void modifyInputContainer(SimpleMutableContainer x){
		x.getMutableClass().setInfo(b.getInfo());
	}
	
	public static void changeImmutableField(SimpleImmutableClass x){
		a = x;
	}
	
	public static void modifyMutableField(String value){
		b.setInfo(value);
	}
	
	public static void modifyMutableClassInContainer(String value){
		c.getMutableClass().setInfo(value);
	}
	
	public static String modifyFieldInputAndReturnData(SimpleMutableClass x, String value){
		b.setInfo(x.getInfo());
		x.setInfo(a.getInfo());
		c.getMutableClass().setInfo(value);
		return value;
	}
	
	public static void changeImmutableFieldWithNewInstance(String value){
		a = new SimpleImmutableClass(value); 
	}
	
	
}
