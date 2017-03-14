package de.tud.cs.peaks.analysis.reflectionUsage;

import java.util.LinkedList;

/**
 * Class that holds the data of the path that has been traversed so far. Used for checking cycles in the analysis.
 * @author moritz
 *
 */
public class SootPath {
	
	public LinkedList<SootPathNode> nodeList;
	
	public SootPath()
	{
		nodeList = new LinkedList<SootPathNode>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SootPath clone(){
		SootPath p = new SootPath();
		p.nodeList = (LinkedList<SootPathNode>) this.nodeList.clone();
		return p;
	}

}
