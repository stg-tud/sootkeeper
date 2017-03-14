package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import java.util.HashMap;

import soot.jimple.toolkits.callgraph.Edge;

public class ThesisRating implements IEdgeRating{
	
	private HashMap<IEdgeRating, Float> ratingModules = new HashMap<IEdgeRating, Float>(){{
		put(new LoCbCRating(), 0.22f);
		put(new DirectParameterDependencyRating(), 0.23f);
		put(new FlowBasedParameterDependencyRating(),0.29f);
		put(new CheckIndicatorRating(),0.26f);
	}};

	@Override
	/**
	 * rates an edge based on the description of bachelors thesis
	 */
	public float rateEdge(Edge edge) {
        
        if(edge.getTgt().method().getSignature().equals("<java.lang.System: void arraycopy(java.lang.Object,int,java.lang.Object,int,int)>")) //assume System.arraycopy as harmless
            return 0.0f;
        
        float weight = 0.0f;
        for(IEdgeRating ratingModule : ratingModules.keySet()){
        	weight += ratingModules.get(ratingModule) * ratingModule.rateEdge(edge);
        }
 
         return weight;
	}

}
