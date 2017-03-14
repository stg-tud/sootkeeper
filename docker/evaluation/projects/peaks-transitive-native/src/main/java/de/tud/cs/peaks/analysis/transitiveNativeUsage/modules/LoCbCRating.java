package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;

public class LoCbCRating implements IEdgeRating {

	@Override
	/**
	 * Returns a rating of 'evilness' of an edge based on the lines of code before the transitive native call in the src method.
	 * @param edge an edge to be rated
	 * @return a rating between 0.0f and 1.0f
	 */
	public float rateEdge(Edge edge) {
        Body activeBody = edge.getSrc().method().getActiveBody();

        PatchingChain<Unit> units = activeBody.getUnits();

        Iterator<Unit> uit = units.iterator();
        int lines = 0;
        while (uit.hasNext()) {
            Unit currentUnit = uit.next();
            lines++;
            if (currentUnit instanceof Stmt) {
                Stmt stmnt = (Stmt) currentUnit;
                if (stmnt.containsInvokeExpr()) {
                    InvokeExpr invkExpr = stmnt.getInvokeExpr();
                    // transitive native call
                    if (invkExpr.getMethod().equals(edge.getTgt().method())) {
                        break;
                    }
                }
            }
        }
        lines -= 1; //ignore jimple assignment of "this"
        lines -= edge.getSrc().method().getParameterCount(); // subtract one for every parameter due to the assignment
        // interpolate by f(x) = 1 - x/15, see bachelors thesis for more details
        return (lines > 15) ? 0 : 1.0f - lines / 15f;
	}

}
