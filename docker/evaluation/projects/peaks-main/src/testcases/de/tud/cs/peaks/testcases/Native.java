package de.tud.cs.peaks.testcases;

public class Native {
	
	
	private Native(){
		
	}
	
	
	public static native int test();
	
	
	public static void main(String[] args){
		Native.test();
	}

}
