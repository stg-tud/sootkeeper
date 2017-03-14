package de.tud.cs.peaks;

import de.tud.cs.peaks.misc.PeaksEdge;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.misc.Visibility;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.PeaksResult;
import soot.Modifier;
import soot.SootMethod;
import soot.Type;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.LinkedList;
import java.util.List;

public abstract class CallGraphAnalysis {

	protected List<PeaksResult> precomputedResults = new LinkedList<PeaksResult>();

    abstract public AnalysisResult performAnalysis(CallGraph cg);
    
    public AnalysisResult performAnalysis(CallGraph cg, List<PeaksResult> precomputedResults){
    	this.precomputedResults = precomputedResults;
    	return performAnalysis(cg);
    }

    protected static PeaksMethod createMethodAbstraction(SootMethod sootMethod, float rating) {
        LinkedList<String> params = new LinkedList<>();
        for (Type type : sootMethod.getParameterTypes()) {
            params.add(type.toString());
        }
        Visibility visibility = getVisibility(sootMethod.getModifiers());
        String name = sootMethod.getName();
        name = name.replace("<", "&lt;");
        name = name.replaceAll(">", "&gt;");
        return new PeaksMethod(name, sootMethod.getReturnType().toString(), visibility, sootMethod.getDeclaringClass().toString(), rating, params);
    }

    protected static PeaksEdge createEdgeAbstraction(Edge edge) {
        PeaksMethod source = createMethodAbstraction(edge.src(), 0.0f);
        PeaksMethod target = createMethodAbstraction(edge.tgt(), 0.0f);
        return new PeaksEdge(source, target);
    }

    protected static Visibility getVisibility(int sootModifier) {
        if (Modifier.isPublic(sootModifier)) {
            return Visibility.Public;
        } else if (Modifier.isProtected(sootModifier)) {
            return Visibility.Protected;
        } else if (Modifier.isPrivate(sootModifier)) {
            return Visibility.Private;
        } else {
            return Visibility.Default;
        }

    }
}
