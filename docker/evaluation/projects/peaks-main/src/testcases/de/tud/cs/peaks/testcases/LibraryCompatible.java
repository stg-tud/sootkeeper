package de.tud.cs.peaks.testcases;

public class LibraryCompatible {

	
	public static void main(String[] args) {
		LibraryCompatible lc = new LibraryCompatible();
		lc.calledByMain(5);
	}
	
	private int calledByMain(int a){
		return a+5;
	}
	
	public int addFive(int a){
		return a+5;
	}
	
	public int addTen(int a){
		return addFive(addFive(a));
	}
}
