package de.tud.cs.peaks.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.Chain;

public class LibraryCompatibleTest extends SootTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
        createScene("de.tud.cs.peaks.testcases.LibraryCompatible").getCallGraph();
	}

	@AfterClass
	public static void tearDown() throws Exception {
        G.reset();
	}

	@Test
	public void classInScene() {
		Chain<SootClass> classes = Scene.v().getApplicationClasses();
		assertEquals(1, classes.size());
		assertEquals("de.tud.cs.peaks.testcases.LibraryCompatible", classes.getFirst().getName());
	}
	
	@Test
	public void methodsInScene(){
		SootClass testClass = Scene.v().getApplicationClasses().getFirst();
		List<SootMethod> methods = testClass.getMethods();
		List<String> actualMethodNames = new LinkedList<String>();
		for(SootMethod m : methods) actualMethodNames.add(m.getName());
		// list of expected methods
		List<String> expectedMethodNames = Arrays.asList(new String[] { "main","calledByMain","addFive","addTen" });
		
		assertTrue(actualMethodNames.containsAll(expectedMethodNames));
	}


    @Test
    public void edgesInCG(){
    	List<SootMethod> methods = Scene.v().getApplicationClasses().getFirst().getMethods();
		
    	SootMethod main = null;
    	SootMethod calledByMain = null;
    	SootMethod addFive = null;
    	SootMethod addTen = null;
		
    	for(SootMethod m : methods){
    		switch (m.getName()) {
			case "main":
				main = m;
				break;
			case "calledByMain":
				calledByMain = m;
				break;
			case "addFive":
				addFive = m;
				break;
			case "addTen":
				addTen = m;
			}
    	}
    	
    	assertTrue(main != null);
    	assertTrue(calledByMain != null);
    	assertTrue(addFive != null);
    	assertTrue(addTen != null);
    	
        CallGraph cg = Scene.v().getCallGraph();
        // callers of calledByMain => only main
        List<SootMethod> callersOfCBM = getSrcs(cg.edgesInto(calledByMain));
        assertEquals(1, callersOfCBM.size());
        assertEquals("main", callersOfCBM.get(0).getName());
        // vice versa
        List<SootMethod> calledByMainVV = getTgts(cg.edgesOutOf(main));    	
        assertTrue(calledByMainVV.contains(calledByMain));
        
        // now methods without main as entry points -> e.g. libraries
        List<SootMethod> calleesOfaddFive = getSrcs(cg.edgesInto(addFive));    	
        assertTrue(calleesOfaddFive.contains(addTen));
        // vice versa
        List<SootMethod> calledByAddTen = getTgts(cg.edgesOutOf(addTen));    	
        assertTrue(calledByAddTen.contains(addFive));
        
    }
    
    /**
     *  helper: gets all src methods as list of an edge iterator
     * @param edges
     * @return
     */
    private List<SootMethod> getSrcs(Iterator<Edge> edges){
    	List<SootMethod> methods = new LinkedList<SootMethod>();
    	while(edges.hasNext()){
    		Edge e = edges.next();
    		if(e != null) methods.add(e.getSrc().method());
    	}
    	return methods;
    }
    /**
     * helper: gets all target methods as list of an edge iterator
     * @param edges
     * @return
     */
    private List<SootMethod> getTgts(Iterator<Edge> edges){
    	List<SootMethod> methods = new LinkedList<SootMethod>();
    	while(edges.hasNext()){
    		Edge e = edges.next();
    		if(e != null) methods.add(e.getTgt().method());
    	}
    	return methods;
    }
}
