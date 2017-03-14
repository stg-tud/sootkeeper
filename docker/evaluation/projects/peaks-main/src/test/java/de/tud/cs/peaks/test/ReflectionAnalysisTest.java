package de.tud.cs.peaks.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import soot.G;
import soot.jimple.toolkits.callgraph.CallGraph;
import de.tud.cs.peaks.results.AnalysisResult;

public class ReflectionAnalysisTest extends SootTest{

	private static AnalysisResult reflectionResult;
	
	@BeforeClass
	public static void setUp() throws Exception {
		CallGraph callGraph = createScene("de.tud.cs.peaks.testcases.SimpleReflection").getCallGraph();
		reflectionResult = new de.tud.cs.peaks.analysis.reflectionUsage.ReflectionAnalysis().performAnalysis(callGraph);
}
	
	@AfterClass
	public static void tearDown() throws Exception{
		reflectionResult = null;
		G.reset();
	}
	
	@Test
    @Ignore
	public void SimpleReflectionTest() {
		assertNotNull(reflectionResult);
		assertTrue(reflectionResult.getCalls().size() > 0);
	}
	
	@Test
	public void InfoStringTest() {
		StringBuilder sb = new StringBuilder();
		
		appendInfoString(sb, "Analysis Version: ", reflectionResult.getAnalysisVersion());
		appendInfoString(sb, "Type: ", reflectionResult.getType().toString());
		appendInfoString(sb, "Fields: ", reflectionResult.getAllFields().toString());
		appendInfoString(sb, "Methods: ", reflectionResult.getAllMethods().toString());
		appendInfoString(sb, "Calls: ", reflectionResult.getCalls().toString());
		appendInfoString(sb, "Paths: ", reflectionResult.getPaths().toString());
		appendInfoString(sb, "private methods: ", reflectionResult.getPrivateMethods().toString());
		
		System.out.println(sb.toString());
		assertNotNull(reflectionResult);
	}
	
	public void appendInfoString(StringBuilder sb, String name, String value){
		sb.append(name);
		sb.append(value);
		sb.append(System.lineSeparator());
	}
}
