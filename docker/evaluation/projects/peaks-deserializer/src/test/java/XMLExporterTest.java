import de.tud.cs.peaks.exporting.xml.PeaksResultSerializer;
import de.tud.cs.peaks.exporting.xml.ResultSerializer;
import de.tud.cs.peaks.importing.xml.PeaksResultDeserializer;
import de.tud.cs.peaks.importing.xml.ResultDeserializer;
import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.misc.PeaksMethod;
import de.tud.cs.peaks.misc.Visibility;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.AnalysisType;
import de.tud.cs.peaks.results.Path;
import de.tud.cs.peaks.results.PeaksResult;
import nu.xom.ParsingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class XMLExporterTest {

	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz"
			.toCharArray();
	// Seed Or Not?
	private static final long SEED = 0xda6f27bdb7563a56L;
	private static Random random;

	@BeforeClass
	public static void setUp() {

	}

	private static PeaksResult createPeaksResult() {
		long duration = random.nextLong();
        String jarName =  createRandomString(6);
        String machine = createRandomString(25);
        return new PeaksResult(duration,machine,jarName);
	}

	private static AnalysisResult createResult() {
		AnalysisResult result = new AnalysisResult("1.0",
				AnalysisType.STATIC_USAGE_ANALYSIS_RESULT);
		for (int i = 0; i < 2500; i++) {
			double type = random.nextDouble();
			if (type < 0.5) { // method
				result.addMethod(createMethod());
			} else if (type < 0.8) { // field
				result.addField(createField());
			} else if (type < 0.85) { // immutable field
				result.addImmutableField(createField());
			} else if (type < 0.9) { // side effect free method
				result.addSideEffectFreeMethod(createMethod());
			} else { // path
				result.setPaths(createPath());
			}

		}
		return result;
	}

	private static Set<Path> createPath() {
		return new HashSet<>();
	}

	private static PeaksField createField() {
		Visibility visibility = randomVisibility();
		float rating = random.nextFloat();
		String name = createRandomString(10);
		String path = createRandomString(20);
        String type = createRandomString(10);
		return new PeaksField(name, visibility, path,type, rating);
	}

	private static String createRandomString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char c = CHARS[random.nextInt(CHARS.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	private static PeaksMethod createMethod() {
		Visibility visibility = randomVisibility();
		float rating = random.nextFloat();
		String name = createRandomString(10);
		String returnValue = createRandomString(6);
		String path = createRandomString(20);
		List<String> params = new LinkedList<>();
		int numLoops = random.nextInt(10);
		for (int i = 0; i < numLoops; i++) {
			params.add(createRandomString(6));
		}
		return new PeaksMethod(name, returnValue, visibility, path, rating,
				params);

	}

	private static Visibility randomVisibility() {
		switch (random.nextInt(3)) {
		case 0:
			return Visibility.Public;
		case 1:
			return Visibility.Protected;
		case 2:
			return Visibility.Default;
		default:
			return Visibility.Public;
		}
	}

	@Before
	public void before() {
		random = new Random(SEED);
	}

	@Test
	public void testXMLExport() throws ParsingException, IOException {
		for (int i = 0; i < 100; i++) {
			AnalysisResult result = createResult();
			String xml = new ResultSerializer(result).toXML();
			ResultDeserializer deserializer = new ResultDeserializer(xml);
			AnalysisResult desResult = deserializer.getResult();
			Assert.assertEquals("Run: " + i, result, desResult);
		}
	}

    @Test
    public void testMetaExport() throws ParsingException, IOException {
        PeaksResult result = createPeaksResult();
        String xml = new PeaksResultSerializer(result).toXML();
        PeaksResultDeserializer deserializer = new PeaksResultDeserializer(xml);
        PeaksResult desResult = deserializer.getResult();
        Assert.assertEquals(result,desResult);
    }
}
