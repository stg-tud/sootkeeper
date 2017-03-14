package de.tud.cs.peaks.test;

import static de.tud.cs.peaks.misc.Visibility.Public;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.jimple.toolkits.callgraph.CallGraph;
import de.tud.cs.peaks.analysis.staticUsage.StaticUsageAnalysis;
import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.results.AnalysisResult;

public class StaticTest extends SootTest {
	private static AnalysisResult staticResult;

	@BeforeClass
	public static void setUp() throws Exception {
		CallGraph callGraph = createScene(
				"de.tud.cs.peaks.testcases.SimpleStatic").getCallGraph();
		staticResult = new StaticUsageAnalysis().performAnalysis(callGraph);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		staticResult = null;
		G.reset();
	}

	@Test
	public void ImmutableFieldsTest() {
		PeaksField[] immutableFields = { new PeaksField("state6", Public,
				"de.tud.cs.peaks.testcases.SimpleStatic",
				"de.tud.cs.peaks.testcases.Immutable", 1.0f) };
		PeaksField[] mutableFields = {
				new PeaksField("state5", Public,
						"de.tud.cs.peaks.testcases.SimpleStatic",
						"java.lang.String", 1.0f),
				new PeaksField("state7", Public,
						"de.tud.cs.peaks.testcases.SimpleStatic",
						"de.tud.cs.peaks.testcases.Mutable", 1.0f) };
		assertNotNull(staticResult);

		assertTrue(staticResult.getImmutableFieldsFound().containsAll(
				Arrays.asList(immutableFields)));
		assertFalse(staticResult.getImmutableFieldsFound().containsAll(
				Arrays.asList(mutableFields)));
	}

}
