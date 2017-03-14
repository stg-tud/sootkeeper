package de.tud.cs.peaks.analysis.transitiveNativeUsage;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * Information about transitively called native methods
 * @author Tim Kranz
 */
public class NativeLink {
	private SootMethod targetNative = null;
	private int distance = -1;
	private Edge edge;
	
	private float rating = Float.NaN;

	/**
	 * Create a link, which is similar to a 'rounting table entry', to hint a transitive native call
	 * @param targetNative The native method is called transitively
	 * @param distance the number of hops before calling the native method over this link
	 * @param edge this edge to hint the path
	 */
	public NativeLink(SootMethod targetNative, int distance, Edge edge) {
		this.targetNative = targetNative;
		this.distance = distance;
		this.edge = edge;
	}
	
	/**
	 * 
	 * @return number of hops between current and target method
	 */
	public int getDistance() {
		return distance;
	}
	
	/**
	 *
	 * @return native method which is transitively called
	 */
	public SootMethod getTargetNative() {
		return targetNative;
	}
	
	/**
	 * 
	 * @return path to transitively call a native method
	 */
	public Edge getEdge() {
		return edge;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edge == null) ? 0 : edge.hashCode());
		result = prime * result
				+ ((targetNative == null) ? 0 : targetNative.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NativeLink other = (NativeLink) obj;
		if (edge == null) {
			if (other.edge != null)
				return false;
		} else if (!edge.equals(other.edge))
			return false;
		if (targetNative == null) {
			if (other.targetNative != null)
				return false;
		} else if (!targetNative.equals(other.targetNative))
			return false;
		return true;
	}
	
	public boolean setRatingOnce(float rating){
		if(this.rating != Float.NaN){
			return false;
		}
		this.rating = rating;
		return true;
	}
	
	
}