package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import soot.jimple.toolkits.callgraph.Edge;

public interface IEdgeRating {

	public float rateEdge(Edge edge);
}
