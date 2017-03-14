package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;

public class CheckIndicatorRating implements IEdgeRating{

	@Override
	/**
	 * Rates an edge based on 'filter indicators' before the transitive
	 * call in the src method.
	 * @param edge an edge to be rated
	 * @return 0.0f, if a method which indicates filtering is called before native call, otherwise 1.0f
	 */
	public float rateEdge(Edge edge) {
		
		SootMethod srcM = edge.getSrc().method();
		if(!srcM.hasActiveBody()) return 1.0f;
		
        Body activeBody = srcM.getActiveBody();
        PatchingChain<Unit> units = activeBody.getUnits();

        Iterator<Unit> uit = units.iterator();

        while (uit.hasNext()) {
            Unit currentUnit = uit.next();
            if (currentUnit instanceof Stmt) {
                Stmt stmnt = (Stmt) currentUnit;
                if (stmnt.containsInvokeExpr()) {
                    InvokeExpr invkExpr = stmnt.getInvokeExpr();
                    if (invkExpr.getMethod().equals(edge.getTgt().method())) return 1.0f; //reached transitive call

                    String methodName = invkExpr.getMethod().getSignature();
                    if (methodName.contains("verify") || methodName.contains("Verify") || methodName.contains("check") || methodName.contains("Check") || methodName.contains("filter") || methodName.contains("Filter")) return 0.0f;
                }
            }
        }
        return 1.0f;
	}

}
