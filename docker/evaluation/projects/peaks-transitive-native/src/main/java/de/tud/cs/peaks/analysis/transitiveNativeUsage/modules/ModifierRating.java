package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;

public class ModifierRating implements IEdgeRating {

	@Override
	/**
	 * Returns a rating based on the visibility of src and tgt method of the call.
	 * @param edge an edge to be rated
	 * @return a rating between 1.0f and 0.0f
	 */
	public float rateEdge(Edge edge) {
        SootMethod tgt = edge.getTgt().method();
        
        // if target is native and private we are happy!
        // src might be a filtering 'wrapper' method
        if(tgt.isNative() && tgt.isPrivate())
            return 0.0f;
        
        int levelSrc = getModifierLevel(edge.getSrc().method());
        int levelTgt = getModifierLevel(tgt);
        if (levelSrc < levelTgt) // e.g. public < private
            return 1.0f; // src is 'more public' than tgt
        return 0.0f;
	}
	
	/**
	 * Maps a modifier level to a number, based on the visibility. 3 (private) > 2 (default) > 1 (protected) > 0 (public)
	 * @param m a sootMethod
	 * @return a number from {0,1,2,3}, indicates the 'visibility' of a modifier
	 */
    private int getModifierLevel(SootMethod m) {
        if (m.isPrivate()) return 3;
        if (m.isProtected()) return 1;
        if (m.isPublic()) return 0;
        return 2;
    }

}
