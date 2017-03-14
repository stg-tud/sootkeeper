package de.tud.cs.peaks.analysis;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.results.AnalysisResult;

@Deprecated
public class FullPrintAnalysis extends CallGraphAnalysis {

	@Override
	public AnalysisResult performAnalysis(CallGraph cg) {
		for (SootClass c : Scene.v().getClasses()) {

			for (SootMethod m : c.getMethods()) {
				System.out.println(c.toString() + "." + m.toString()
						+ " hasActiveBody: " + m.hasActiveBody());
			}
		}

		return null;
	}

}
