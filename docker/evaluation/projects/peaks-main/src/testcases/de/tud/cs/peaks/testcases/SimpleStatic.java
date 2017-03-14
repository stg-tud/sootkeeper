package de.tud.cs.peaks.testcases;

public class SimpleStatic {
	private int state1;
	private static double state2;
	public int state3;
	public static double state4;
    public static String state5 = "state";
	public static final Immutable state6 =  new Immutable();
    protected static final Mutable state7 = new Mutable();
	private int method1() { return 0; }
	private static int method2() { return 0; }
	public int method3() { return 0; }
	public static int method4() { return 0; }
    public static void method5() {
       state7.changeInt();
    }
    public static void method6(){
        System.out.println();
        state5 = "changedState";
    }

    public static void main(String[] args) {
        method2();
        method4();
        method5();
        method6();
        SimpleStatic s = new SimpleStatic();
        s.method1();
        s.method3();
    }
}
