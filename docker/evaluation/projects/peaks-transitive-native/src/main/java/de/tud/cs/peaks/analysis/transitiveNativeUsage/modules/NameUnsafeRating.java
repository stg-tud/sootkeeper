package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;

public class NameUnsafeRating implements IEdgeRating{

	@Override
	/**
	 * Very simple metrics: Rates an edges evilness based on indication phrases in name, e.g. 'unsafe'
	 * @param edge an edge to rate
	 * @return 1.0f if name suggest to be unsafe, otherwise 0.0f
	 */
	public float rateEdge(Edge edge) {
        SootMethod m = edge.src().method();
        // very simple, other phrases can be added :)
        if (m.getSignature().contains("Unsafe") || m.getSignature().contains("unsafe")) {
            return 1.0f;
        } else {
            return 0.0f;
        }
	}

}
