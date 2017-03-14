package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.Body;
import soot.BooleanType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.PatchingChain;
import soot.PrimType;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.typing.fast.Integer32767Type;

public class DirectParameterDependencyRating implements IEdgeRating {

	@Override
	/**
	 * Rates an edges evilness based on the number and type of directly 
	 * depending parameters. More complex, directly taken, variables cause 
	 * possibly a higher risk than e.g. simple booleans.
	 * 
	 * @param edge an edge to be rate
	 * 
	 *  @return the 'evilness' between 0.0f and 1.0f of a method
	 */
	public float rateEdge(Edge edge) {
		
		SootMethod srcM = edge.getSrc().method();
		if(!srcM.hasActiveBody()) return 1.0f;
				
		Body activeBody = srcM.getActiveBody();

		Set<Local> parameterInput = new HashSet<Local>();

		for (int i = 0; i < edge.getSrc().method().getParameterCount(); i++) {
			parameterInput.add(activeBody.getParameterLocal(i));
		}
		if (parameterInput.size() == 0)
			return 0.0f;

		PatchingChain<Unit> units = activeBody.getUnits();

		Iterator<Unit> uit = units.iterator();

		while (uit.hasNext()) {
			Unit currentUnit = uit.next();

			if (currentUnit instanceof Stmt) {
				Stmt stmnt = (Stmt) currentUnit;
				if (stmnt.containsInvokeExpr()) {
					InvokeExpr invkExpr = stmnt.getInvokeExpr();
					if (invkExpr.getMethod().equals(edge.getTgt().method())) {
						Float forwarded = 0.0f;
						for (Value arg : invkExpr.getArgs()) {
							if (parameterInput.contains(arg)) {
								Type t = arg.getType();
								// sum up the 'evilness' (type rating per
								// parameter)
								forwarded += getTypeRating(t);
							}
						}
						// we limit the sum of 'evilness' to 1.0f
						return Math.min(1.0f, forwarded);
					}
				}
			}
		}
		return 0.0f;
	}

	/**
	 * Returns a weight of a variable type. General idea: More complex types
	 * allow a greater surface for attacks
	 * 
	 * @param t
	 *            A soot-type to rate
	 * @return a rating between 0.0f and 1.0f
	 */
	private Float getTypeRating(Type t) {
		if (t instanceof LongType || t instanceof IntType
				|| t instanceof Integer32767Type) {
			// long, int, Integer = overflow, addresses
			return 0.5f;
		} else if (t instanceof BooleanType) {
			// bool
			return 0.1f;
		} else if (t instanceof PrimType) {
			// Byte, Char, Double, Float, Short
			return 0.3f;
		} else {
			return 0.8f;
		}
	}

}
