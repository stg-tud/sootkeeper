package de.tud.cs.peaks.analysis.transitiveNativeUsage;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.Chain;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.analysis.transitiveNativeUsage.modules.IEdgeRating;
import de.tud.cs.peaks.analysis.transitiveNativeUsage.modules.ThesisRating;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.AnalysisType;
import de.tud.cs.peaks.results.PeaksResult;

public class TransitiveNativeUsage extends CallGraphAnalysis {
	private final String VERSION = "0.1";

	@Override
	public AnalysisResult performAnalysis(CallGraph cg) {
		// get native methods to use them as entry points
		HashSet<SootMethod> nativeMethods = getNativeMethods();
		
		for(PeaksResult pr : precomputedResults){
			Collection<PeaksMethod> precomputedMethods = pr.getTransitveNativeUsageResult().getAllMethods();
			for(PeaksMethod pMethod : precomputedMethods){
				SootMethod sMethod  = Scene.v().getMethod("<"+pMethod.getQualifiedName()+">");
				nativeMethods.add(sMethod);
			}
		}
		
		// rate all edges of transitive hull
		HashMap<Edge, Float> callRatings = callRatings(cg, nativeMethods,
				new ThesisRating());
		// use fixed point iteration to rate all methods of transitive hull
		return traverseAndRate(cg, nativeMethods, callRatings);
	}

	/**
	 * Traverses the call graph and uses entryPoints as start point to enter the
	 * analysis. For the rating, a fixed-point iteration is used, whereas the
	 * rating of the edges needs to be set in callRatings. Continuous Rating:
	 * 1.0f high risk to 0.0f for 'safe' methods
	 * 
	 * @param cg
	 *            A Soot call Graph to be traversed
	 * @param entryPoints
	 *            the entryPoints to start the analysis at. They are rated by
	 *            1.0f. E.g. for native analysis all native methods
	 * @param callRatings
	 *            an associated rating of edges
	 * @return AnalysisResult for native methods
	 */
	private AnalysisResult traverseAndRate(CallGraph cg,
			HashSet<SootMethod> entryPoints, HashMap<Edge, Float> callRatings) {

		AnalysisResult result = new AnalysisResult(VERSION,
				AnalysisType.TRANSITIVE_NATIVE_USAGE_RESULT);

		HashMap<SootMethod, Float> rating = new HashMap<SootMethod, Float>();
		// rate all entry points by maximum threat (they should be direct
		// native)
		for (SootMethod nativeMethod : entryPoints) {
			rating.put(nativeMethod, 1.0f);
		}

		LinkedList<SootMethod> queueUpdated = new LinkedList<SootMethod>(
				entryPoints);
		// now fixed-point iteration
		while (!queueUpdated.isEmpty()) {
			/**
			 * We iterate from bottom to top, so srcM : method to be rated
			 * (caller) | call / edge: propagates the threat ↓ tgtM : method
			 * already rated (callee)
			 */
			SootMethod tgtM = queueUpdated.poll();
			if (tgtM == null)
				continue;

			if (!rating.containsKey(tgtM)) {
				// should never be reached
				System.err.println("[Transitive Native] Missing Src Rating: " + tgtM);
				continue;
			}
			// all calls of this method
			Iterator<Edge> edges = cg.edgesInto(tgtM);

			while (edges.hasNext()) {
				Edge edge = edges.next();
				if (edge == null)
					continue;
				if (!callRatings.containsKey(edge)) {
					// should never be reached
					System.err.println("Transitive Native] Missing Edge Rating: " + edge);
					continue;
				}

				SootMethod srcM = edge.src().method();

				float oldRating = rating.containsKey(srcM) ? rating.get(srcM)
						: 0.0f;
				float newRating = callRatings.get(edge) * rating.get(tgtM);
				// the new call has a higher risk -> assume it
				// we always search for the worst case
				if (newRating > oldRating) {
					rating.put(srcM, newRating);
					// value has changed -> all callees need to be reevaluated
					queueUpdated.add(srcM);
				}
			}

		}
		for (SootMethod method : rating.keySet()) {
			result.addMethod(createMethodAbstraction(method, rating.get(method)));
		}

		return result;
	}

	/**
	 * Calculates the weight of all edges in the transitive hull
	 * 
	 * @param cg
	 *            a soot CallGraph to be traversed
	 * @param entryPoints
	 *            a set of all entry points, e.g. native methods
	 * @param ratingModule
	 *            an edge rating module
	 * @return a map of all methods of the hull an a rating evaluated by the
	 *         rating module
	 */
	private HashMap<Edge, Float> callRatings(CallGraph cg,
			HashSet<SootMethod> entryPoints, IEdgeRating ratingModule) {

		LinkedList<SootMethod> queue = new LinkedList<SootMethod>();

		// tag transitive hull
		queue.addAll(entryPoints);

		// store all visited methods to prevent loops
		HashSet<SootMethod> transitiveHull = new HashSet<SootMethod>();
		HashMap<Edge, Float> callRatings = new HashMap<Edge, Float>();

		while (!queue.isEmpty()) {
			SootMethod currentMethod = queue.poll();
			if(currentMethod == null) continue;
			Iterator<Edge> edges = cg.edgesInto(currentMethod);
			while (edges.hasNext()) {
				Edge currentEdge = edges.next();
				if (currentEdge == null)
					continue;

				SootMethod src = currentEdge.getSrc().method();

				if (!callRatings.containsKey(currentEdge)) {
					float f = ratingModule.rateEdge(currentEdge);
					callRatings.put(currentEdge, f);
				}

				if (!transitiveHull.contains(src)) {
					transitiveHull.add(src);
					queue.add(src);
				}
			}
		}

		return callRatings;
	}

	/**
	 * Takes the scene and searches all native methods
	 * 
	 * @return a Set of native methods
	 */
	private HashSet<SootMethod> getNativeMethods() {
		// get native methods
		Chain<SootClass> classes = Scene.v().getApplicationClasses();
		HashSet<SootMethod> nativeMethods = new HashSet<SootMethod>();
		for (SootClass c : classes) {
			for (SootMethod m : c.getMethods()) {
				if (m.isNative())
					nativeMethods.add(m);
			}
		}
		return nativeMethods;
	}
}
