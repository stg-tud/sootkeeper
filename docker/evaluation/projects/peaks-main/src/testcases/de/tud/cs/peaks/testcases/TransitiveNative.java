package de.tud.cs.peaks.testcases;


public class TransitiveNative {

	public static native int nativeStuff();
	
	public static void invokeLevel1(){
		nativeStuff();
	}
	
	public static void invokeLevel2(){
		invokeLevel1();
	}
	
	public static void other() {
		System.out.println(StrictMath.asin(3.14159d));
	}

	public static void other2() {
		int a = 021;
		if ((0x2 + 17 - a) == 0b10) {
			other();
		} else {
			System.out.println("not invoked");
		}
	}
	
	public static void main(String[] args) {
		other2();
		invokeLevel2();
	}

}
