package de.tud.cs.peaks.analysis;

import java.util.HashSet;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.results.AnalysisResult;

@Deprecated
public class NoActiveBodyAnalysis extends CallGraphAnalysis {

	@Override
	public AnalysisResult performAnalysis(CallGraph cg) {
		HashSet<SootMethod> reLoad = new HashSet<SootMethod>();
		for (SootClass c : Scene.v().getClasses()) {
			if (c.isJavaLibraryClass())
				continue;
			for (SootMethod m : c.getMethods()) {
				if (!m.hasActiveBody()) {
					reLoad.add(m);
				}
			}
		}

		System.out.println("before: " + reLoad.size());

		HashSet<SootMethod> afterOneRun = new HashSet<SootMethod>();
		for (SootMethod m : reLoad) {
			if (m.hasActiveBody())
				continue;
			SootClass c = m.getDeclaringClass();

			System.out.println("trying for " + m.toString());
			SootClass cNew = Scene.v().forceResolve(c.getName(),
					SootClass.BODIES);
			Scene.v().loadNecessaryClasses();
			Scene.v().loadDynamicClasses();

			SootMethod mNew = cNew.getMethod(m.getSubSignature());

			if (!mNew.hasActiveBody()) {
				System.out.println("nope");
				afterOneRun.add(mNew);
			}

		}

		System.out.println("before: " + reLoad.size() + " - after: "
				+ afterOneRun.size());

		return null;
	}

}
