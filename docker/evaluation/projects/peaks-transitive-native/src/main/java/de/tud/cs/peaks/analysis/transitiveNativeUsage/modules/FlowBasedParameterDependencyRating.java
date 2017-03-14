package de.tud.cs.peaks.analysis.transitiveNativeUsage.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.toolkits.callgraph.Edge;

public class FlowBasedParameterDependencyRating implements IEdgeRating {

	private HashMap<Value, Float> recRatingCache;
	
	@Override
	/**
	 * Rates an edge based on transitive parameter dependencies
	 */
	public float rateEdge(Edge edge) {
		Body activeBody = edge.getSrc().method().getActiveBody();
		
        HashMap<Value, LinkedList<Value>> predValue = new HashMap<Value, LinkedList<Value>>();
        for (int i = 0; i < edge.getSrc().method().getParameterCount(); i++) {
            predValue.put(activeBody.getParameterLocal(i), null);
        }

        if (predValue.isEmpty()) // method has no parameters
            return 0.0f;

        PatchingChain<Unit> units = activeBody.getUnits();
        Iterator<Unit> uit = units.iterator();

        Set<Value> conditionVars = new HashSet<Value>();

        while (uit.hasNext()) {
            Unit currentUnit = uit.next();
            if (currentUnit instanceof JIfStmt) {
                JIfStmt jif = (JIfStmt) currentUnit;
                for (ValueBox ub : (List<ValueBox>) jif.getCondition().getUseBoxes()) {
                    conditionVars.add(ub.getValue());
                }

            }
            if (!currentUnit.getDefBoxes().isEmpty()) {
                Value leftVar = currentUnit.getDefBoxes().get(0).getValue(); // var name

                if (leftVar instanceof JInstanceFieldRef) { // e.g. for casts
                    JInstanceFieldRef jinfr = (JInstanceFieldRef) leftVar;
                    leftVar = jinfr.getBase();
                }

                for (ValueBox rightVar : currentUnit.getUseBoxes()) {
                    if (predValue.containsKey(rightVar.getValue())) {
                        // current parameter has a parameter dependency
                        if (predValue.containsKey(leftVar)) {
                            if (predValue.get(leftVar) == null) continue;
                            if (leftVar.equals(rightVar.getValue())) continue;
                            predValue.get(leftVar).add(rightVar.getValue());
                        } else {
                            LinkedList<Value> lofPred = new LinkedList<Value>();
                            lofPred.add(rightVar.getValue());
                            predValue.put(leftVar, lofPred);
                        }
                    }
                }
            }

            if (currentUnit instanceof Stmt) { // invoke
                Stmt stmt = (Stmt) currentUnit;
                if (stmt.containsInvokeExpr()) {
                    InvokeExpr invkExpr = stmt.getInvokeExpr();
                    if (invkExpr.getMethod().equals(edge.getTgt().method())) {
                        Float res = 0.0f;
                        for (Value arg : invkExpr.getArgs()) {
                            if (predValue.containsKey(arg)) {
                                recRatingCache = new HashMap<Value, Float>();
                                res += getRecursiveVarRating(arg, predValue, conditionVars, new HashSet<Value>());
                            }
                        }
                        return Math.min(1.0f, res);
                    }
                }
            }

        }
        return 0.0f;
	}

	
	
    private float getRecursiveVarRating(Value var, HashMap<Value, LinkedList<Value>> dependency, Set<Value> conditionVars, Set<Value> visited) {
        if (visited.contains(var)) return 0.0f;
        if(recRatingCache.containsKey(var)) return recRatingCache.get(var);
        LinkedList<Value> pred = dependency.get(var);
        if (pred == null) return 1.0f; //parameter
        if (pred.isEmpty()) return 0.0f; //no parameters
        Float res = 0.0f;
        for (Value val : pred) {
            if(visited.contains(val)) continue;
            if(recRatingCache.containsKey(val)) return recRatingCache.get(val);
            Float mult = 0.6f;
            if (conditionVars.contains(val)) {
                mult = 0.2f;
            }
            visited.add(var);
            res += mult * getRecursiveVarRating(val, dependency, conditionVars, visited);
            recRatingCache.put(val, res);
            visited.remove(var);
        }

        return res;
    }
}
