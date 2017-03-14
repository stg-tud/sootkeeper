package de.tud.cs.peaks.analysis.reflectionUsage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.AnalysisType;
import de.tud.cs.peaks.results.Path;

/**
 * 
 * @author Moritz Tiedje Find Reflections in the code and determine whether they
 *         are intrusive or not.
 */
public class ReflectionAnalysis extends CallGraphAnalysis {

	// TODO: Class Variables?

	/**
	 * Recursive Analysis of a possible intrusive reflection and its transitive
	 * propagation in calling methods
	 * 
	 * @param cg
	 *            The complete CallGraph of the Program
	 * @param currentMethod
	 *            The method whose body is analyzed in the current iteration
	 * @param currentUnit
	 *            A unit in the currentMethod, that contains a reflection call
	 *            or a reference to a dangerous method
	 * @param sootPath
	 *            The path from the original reflection to the current error
	 * @param valuesToAnalyze
	 *            The Values, if any, in the currentMethod that need to be
	 *            inspected
	 */
	private void completeAnalysis(CallGraph cg, SootMethod currentMethod,
			Unit currentUnit, SootPath sootPath,
			HashSet<Value> valuesToAnalyze, Set<Path> results) {
		// Update CallPath
		// LineNumberTag currentLineNumber = (LineNumberTag)
		// (currentUnit.getTag("LineNumberTag"));
		sootPath.nodeList.add(new SootPathNode(currentUnit, currentMethod));

		// Create Unit Graph of Method Body
		ExceptionalUnitGraph ug = new ExceptionalUnitGraph(
				currentMethod.getActiveBody());

		// Perform Analysis on Unit Graph
		ReflectedVariableFlowAnalysis analysis;
		if (valuesToAnalyze == null)
			analysis = new ReflectedVariableFlowAnalysis(ug);
		else
			analysis = new ReflectedVariableFlowAnalysis(ug, valuesToAnalyze);

		// Extract Result from Analysis
		Unit firstUnit = currentMethod.getActiveBody().getUnits().getFirst();
		HashSet<Value> res = analysis.getFlowBefore(firstUnit);

		// Get indices of relevant parameters
		LinkedList<Short> usedParameters = new LinkedList<Short>();
		for (Value v : res)
			if (v.toString().contains("@parameter"))
				usedParameters.add(Short.parseShort(v.toString().replaceAll(
						"\\D", "")));

		if (!usedParameters.isEmpty()) {
			if (currentMethod.isPublic() || currentMethod.isProtected()) // Recursion
																			// Anchor
				results.add(convertSootPathToPath(sootPath));

			else {
				// Create loop-free list of methods that call the currentMethod
				LinkedList<SootMethod> callingMethods = new LinkedList<SootMethod>();
				for (Iterator<Edge> eit = cg.edgesInto(currentMethod); eit
						.hasNext();) {
					SootMethod m = eit.next().src();
					boolean methodHasBeenSeen = false;
					for (SootPathNode pn : sootPath.nodeList)
						if (pn.method.equals(m))
							methodHasBeenSeen = true;
					if (!methodHasBeenSeen)
						callingMethods.add(m);
				}

				// Iterate over List of methods calling currentMethod
				for (SootMethod m : callingMethods) {
					Iterator<Unit> uit = m.getActiveBody().getUnits()
							.iterator();
					Unit nextUnit = uit.next();

					// Iterate to the call of currentMethod
					while (uit.hasNext()
							&& (!nextUnit.toString().contains(
									currentMethod.toString()) || nextUnit
									.toString().contains("goto ")))
						nextUnit = uit.next();

					// Determine Variables To Analyze from used parameters
					HashSet<Value> nextVarsToAnalyze = new HashSet<Value>();
					for (Short s : usedParameters) {
						List<ValueBox> boxes = nextUnit.getUseBoxes();
						if (boxes.size() > s)
							nextVarsToAnalyze.add(boxes.get(s).getValue());
					}

					// Take next recursive step
					completeAnalysis(cg, m, nextUnit, sootPath,
							nextVarsToAnalyze, results);
				}
			}
		}

		sootPath.nodeList.removeLast();
	}

	private Path convertSootPathToPath(SootPath p) {
		LinkedList<PeaksMethod> nodes = new LinkedList<>();
		for (SootPathNode spn : p.nodeList) {
			nodes.add(createMethodAbstraction(spn.method,
					(spn.method.isPublic() ? 1.0f : 0.8f)));
		}
		return new Path(nodes);
	}

	@Override
	public AnalysisResult performAnalysis(CallGraph cg) {

		Set<Path> results = new HashSet<Path>();

		Chain<SootClass> classes = Scene.v().getApplicationClasses();

		for (SootClass currentClass : classes) {
			for (SootMethod currentMethod : currentClass.getMethods()) {
				if (currentMethod.hasActiveBody()) {

					PatchingChain<Unit> units = currentMethod.getActiveBody()
							.getUnits();

					Unit currentUnit;
					String currentUnitText;

					for (Unit unit : units) {
						currentUnit = unit;
						currentUnitText = currentUnit.toString();

						// TODO: Figure out a cooler way to check if a unit
						// contains a reflection call

						if (currentUnitText
								.matches("(.*)java\\.lang\\.reflect\\.(.*):(.*)setAccessible\\(boolean\\)(.*)"))
							if (!currentUnitText.contains("goto ")) {
								completeAnalysis(cg, currentMethod,
										currentUnit, new SootPath(), null,
										results);
							}
					}
				}
			}
		}

		AnalysisResult r = new AnalysisResult("1.0",
				AnalysisType.REFLECTION_ANALYSIS_RESULT);
		r.setPaths(results);

		// When debugging, uncomment line below
		// DebugToolkit.saveAll();
		return r;
	}
}
