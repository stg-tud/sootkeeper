package de.tud.cs.peaks.analysis.directNativeUsage;

import java.util.Iterator;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.Chain;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.AnalysisType;

public class DirectNativeUsage extends CallGraphAnalysis {
    private static final String  VERSION = "1.0";
    
    
    public AnalysisResult performAnalysis(CallGraph cg) {
        AnalysisResult nativeResult = new AnalysisResult(VERSION, AnalysisType.DIRECT_NATIVE_USAGE_RESULT);

        Chain<SootClass> classes = Scene.v().getApplicationClasses();

        for (SootClass currentClass : classes) {
            for (SootMethod currentMethod : currentClass.getMethods()) {
                if (currentMethod.isNative()) {
                    nativeResult.addMethod(createMethodAbstraction(currentMethod, 1.0f));

                    Iterator<Edge> edgeIt = cg.edgesInto(currentMethod);
                    while (edgeIt.hasNext()) {
                        Edge edge = edgeIt.next();
                        if (edge == null) break;
                        nativeResult.addCall(createEdgeAbstraction(edge));
                    }
                }
            }
        }
        return nativeResult;
    }

}
