package de.tud.cs.peaks.test;

import java.io.IOException;

import soot.Scene;
import de.tud.cs.peaks.PeaksOptions;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootRun;

public abstract class SootTest {

	public SootTest() {
		super();
	}

	protected static Scene createScene(String testClass) {

		AnalysisTarget target = new AnalysisTarget().addClass(testClass)
				.classPath("./target/classes/");

		FluentOptions opt = PeaksOptions.standard;

		try {
			System.out.println(new java.io.File(".").getCanonicalPath());
		} catch (IOException e) {
			// Abandon all hope... the current directory cannot be read... women
			// and children first!
			e.printStackTrace();
		}
		new SootRun(opt, target).perform();

		return Scene.v();
	}
}