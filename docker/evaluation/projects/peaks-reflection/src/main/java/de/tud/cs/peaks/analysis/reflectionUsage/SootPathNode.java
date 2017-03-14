package de.tud.cs.peaks.analysis.reflectionUsage;

import soot.SootMethod;
import soot.Unit;

public class SootPathNode 
{
	public Unit unit;
	public SootMethod method;
	
	public SootPathNode(Unit u, SootMethod sm)
	{
		this.unit = u;
		this.method = sm;
	}
	
	public String toString()
	{
		return method.toString() + ": " + unit.toString();
	}

}