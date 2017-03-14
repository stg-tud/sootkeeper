package de.tud.cs.peaks.analysis.reflectionUsage;

import soot.*;
import java.util.*;

import soot.toolkits.graph.*;

import soot.toolkits.scalar.BackwardFlowAnalysis;

public class ReflectedVariableFlowAnalysis extends BackwardFlowAnalysis<Unit, HashSet<Value>>{

	private HashSet<Value> genSet = new HashSet<Value>();
	private HashSet<Value> killSet = new HashSet<Value>();
	private HashSet<Value> entrySet;
	
	public ReflectedVariableFlowAnalysis(DirectedGraph<Unit> graph) {
		super(graph);
		
		this.entrySet = new HashSet<Value>();
		doAnalysis();
	}
	
	public ReflectedVariableFlowAnalysis(DirectedGraph<Unit> graph, HashSet<Value> entrySet) {
		super(graph);
		
		this.entrySet = entrySet;	
		doAnalysis();
	}

	//TODO: Instead of 'HashSet<Value> entrySet' give 'List<Short> parameters' & 'SootMethod currentMethod'

	@Override
	protected void flowThrough(HashSet<Value> in, Unit d, HashSet<Value> out) {
		genSet.clear();
		killSet.clear();
		
		//if [d := x.setAccessible(true)] then [x is in GenList]
		if(d.toString().matches("(.*)java\\.lang\\.reflect(.*)setAccessible\\(boolean\\)(.*)") && !d.toString().contains("goto "))
			genSet.add(d.getUseBoxes().get(0).getValue());
		
		//x e in  &&  (d:= x = y + z)  ->  x e KillList & y,z e GenList
		if(!d.getDefBoxes().isEmpty())
		{
			Value x = d.getDefBoxes().get(0).getValue();
			if(in.contains(x))
			{
				killSet.add(x);
				for(ValueBox boxY: d.getUseBoxes())
					genSet.add(boxY.getValue());
			}
		}
		
		//x e in  &&  d:=f(x,y,z)  ->  y,z e GenList
		//TODO: Examine f
		for(ValueBox vb_x: d.getUseBoxes())
			if(in.contains(vb_x.getValue()))  //x e in
			{
				int size = d.getUseBoxes().size();
				String lastEntryTxt = d.getUseBoxes().get(size - 1).toString();
				if(lastEntryTxt.contains("staticinvoke ") || lastEntryTxt.contains("virtualinvoke ")) //d:= f(x,...)
					for(int i = 0; i < size - 1; i++)
						genSet.add(d.getUseBoxes().get(i).getValue());
			}
		
		//out = (in - killList) + GenList;
		out.clear();
		
		for(Value v: in)
			if(!killSet.contains(v))
				out.add(v);
		
		for(Value v: genSet)
			out.add(v);
	}

	@Override
	protected HashSet<Value> newInitialFlow() 
	{
		return new HashSet<Value>();
	}

	@Override
	protected HashSet<Value> entryInitialFlow() 
	{
		return entrySet;
	}

	@Override
	protected void merge(HashSet<Value> in1, HashSet<Value> in2, HashSet<Value> out) 
	{
		out.clear();
		for(Value v: in1)
			out.add(v);
		for(Value v: in2)
			out.add(v);
	}

	@Override
	protected void copy(HashSet<Value> source, HashSet<Value> dest) 
	{
		dest.clear();
		for(Value v: source)
			dest.add(v);
	}
}