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
import soot.Scene;
import de.tud.cs.peaks.analysis.transitiveNativeUsage.TransitiveNativeUsage;
import de.tud.cs.peaks.misc.PeaksEdge;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.results.AnalysisResult;

public class TransitiveNativeTest extends SootTest {

	private static TransitiveNativeUsage tnu;

	@BeforeClass
	public static void setUp() throws Exception {
		createScene("de.tud.cs.peaks.testcases.TransitiveNative").getCallGraph();
		tnu = new TransitiveNativeUsage();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		tnu = null;
		G.reset();
	}

	@Test
	public void nativeMethodsTest() {
		// not part of transitive hull
		PeaksMethod other = new PeaksMethod("other", "void", Public,
				"de.tud.cs.peaks.testcases.TransitiveNative", 1.0f,
				new ArrayList<String>());
		PeaksMethod other2 = new PeaksMethod("other2", "double", Public,
				"java.lang.StrictMath", 1.0f,
				Arrays.asList(new String[] { "java.lang.Double" }));
		PeaksEdge edgeNotContained = new PeaksEdge(other2, other);

		// native method
		PeaksMethod nativeMethod = new PeaksMethod("nativeStuff", "int",
				Public, "de.tud.cs.peaks.testcases.TransitiveNative", 1.0f,
				new ArrayList<String>());
		// transitive hull
		PeaksMethod invokeLevel1 = new PeaksMethod("invokeLevel1", "void",
				Public, "de.tud.cs.peaks.testcases.TransitiveNative", 1.0f,
				new ArrayList<String>());
		PeaksMethod invokeLevel2 = new PeaksMethod("invokeLevel2", "void",
				Public, "de.tud.cs.peaks.testcases.TransitiveNative", 1.0f,
				new ArrayList<String>());
		// main
		PeaksMethod mainMethod = new PeaksMethod("main", "void", Public,
				"de.tud.cs.peaks.testcases.TransitiveNative", 1.0f,
				Arrays.asList(new String[] { "java.lang.String[]" }));
		// calls
		PeaksEdge firstlevelCall = new PeaksEdge(invokeLevel1, nativeMethod);
		PeaksEdge secondlevelCall = new PeaksEdge(invokeLevel2, invokeLevel1);
		PeaksEdge mainCall = new PeaksEdge(mainMethod, invokeLevel2);

		AnalysisResult result = tnu.performAnalysis(Scene.v().getCallGraph());
		assertNotNull(result);

		ArrayList<PeaksMethod> transitiveHull = new ArrayList<PeaksMethod>();
		transitiveHull.add(nativeMethod);
		transitiveHull.add(invokeLevel1);
		transitiveHull.add(invokeLevel2);
		transitiveHull.add(mainMethod);

		assertTrue(result.getAllMethods().containsAll(transitiveHull));

		assertFalse(result.getAllMethods().contains(other));
		assertFalse(result.getAllMethods().contains(other2));

	}

}
