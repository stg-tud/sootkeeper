package de.tud.cs.peaks.test;

import static de.tud.cs.peaks.misc.Visibility.Public;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.jimple.toolkits.callgraph.CallGraph;
import de.tud.cs.peaks.analysis.directNativeUsage.DirectNativeUsage;
import de.tud.cs.peaks.misc.PeaksEdge;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.results.AnalysisResult;

public class SimpleNative extends SootTest {
	private static CallGraph callGraph;
	private static DirectNativeUsage dnu;

	@BeforeClass
	public static void setUp() throws Exception {
		callGraph = createScene("de.tud.cs.peaks.testcases.Native")
				.getCallGraph();
		dnu = new DirectNativeUsage();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		dnu = null;
		callGraph = null;
		G.reset();
	}

	@Test
	public void nativeMethodsTest() {
		PeaksMethod main = new PeaksMethod("main", "void", Public,
				"de.tud.cs.peaks.testcases.Native", 1.0f,
				Arrays.asList(new String[] { "java.lang.String[]" }));
		PeaksMethod nativeMethod = new PeaksMethod("test", "int", Public,
				"de.tud.cs.peaks.testcases.Native", 1.0f,
				new ArrayList<String>()) ;
		PeaksEdge edge = new PeaksEdge(main, nativeMethod);
		AnalysisResult result = dnu.performAnalysis(callGraph);
		assertNotNull(result);
		assertTrue(result.getPublicMethods().containsAll(
				Arrays.asList(nativeMethod)));
		assertTrue(result.getCalls().contains(edge));
	}
	
	
	@Test
	public void ratingTest(){
		PeaksMethod main = new PeaksMethod("main", "void", Public,
				"de.tud.cs.peaks.testcases.Native", 1.0f,
				Arrays.asList(new String[] { "java.lang.String[]" }));
		PeaksMethod nativeMethod=new PeaksMethod("test", "int", Public,
				"de.tud.cs.peaks.testcases.Native", 1.0f,
				new ArrayList<String>()) ;
		AnalysisResult result = dnu.performAnalysis(callGraph);
		
		assertTrue(result.getAllMethods().contains(nativeMethod));
		assertFalse(result.getAllMethods().contains(main));

		for(PeaksMethod method : result.getAllMethods())
			if(method.equals(nativeMethod))
				assertTrue(method.getRating() == 1.0f);
	}

	@Test
	public void OtherTest() {
		System.out.println("TESTED");
	}
}
