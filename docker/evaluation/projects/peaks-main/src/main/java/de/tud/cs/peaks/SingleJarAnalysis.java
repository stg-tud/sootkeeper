package de.tud.cs.peaks;

import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.PeaksResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;
import de.tud.cs.peaks.sootconfig.SootResult;
import de.tud.cs.peaks.sootconfig.SootRun;
import soot.Scene;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static de.tud.cs.peaks.PeaksOptions.standard;

/**
 * @author Patrick Müller
 */
public class SingleJarAnalysis {
	private final List<CallGraphAnalysis> callGraphAnalysisList;
	private final Path pathToJar;
    private List<PeaksResult> precomputedResults;

    public SingleJarAnalysis(List<CallGraphAnalysis> analysisList,
			Path pathToJar) {
		callGraphAnalysisList = analysisList;
		this.pathToJar = pathToJar;
        precomputedResults = new LinkedList<>();
	}

	public SingleJarAnalysis(List<CallGraphAnalysis> analysisList,
			String pathToJar) {
		this(analysisList, Paths.get(pathToJar));
	}

    public SingleJarAnalysis(List<CallGraphAnalysis> analysisList, Path pathToJar,List<PeaksResult> precomputedResults) {
        this(analysisList,pathToJar);
        this.precomputedResults = precomputedResults;
    }

    public SingleJarAnalysis(List<CallGraphAnalysis> analysisList, String pathToJar,List<PeaksResult> precomputedResults) {
        this(analysisList, Paths.get(pathToJar));
        this.precomputedResults = precomputedResults;
    }

    public PeaksResult analyse() {
        return analyse(standard);
    }

	public PeaksResult analyse(FluentOptions o) {
		AnalysisTarget t = new AnalysisTarget().processPath(
				pathToJar.toString()).classPathToProcessPathDirectory();
		SootRun analysisRun = new SootRun(o, t);

		long start = System.currentTimeMillis();

		SootResult baseResult = analysisRun.perform();

		List<AnalysisResult> results = doPeaksAnalyses(baseResult.getScene());

		long duration = System.currentTimeMillis() - start;
		PeaksResult result = buildMetaResult(results, duration);
		return result;
	}

    private List<AnalysisResult> doPeaksAnalyses(Scene scene) {
        final List<AnalysisResult> results = new LinkedList<>();
        for (CallGraphAnalysis analysis : callGraphAnalysisList) {
            results.add(analysis.performAnalysis(scene.getCallGraph(),precomputedResults));
        }
        return results;
    }

	private PeaksResult buildMetaResult(List<AnalysisResult> results,
			long duration) {
		String jarName = getJarName();
		String system = getSystemInformation();
		PeaksResult result = new PeaksResult(duration, system, jarName);
		addResults(result, results);
		return result;
	}

	/*
	 * Builds the System Information String we add to the Result to have
	 * Information about the analysing machine
	 */
	private String getSystemInformation() {
		StringBuilder sb = new StringBuilder();
		sb.append("Java Version: ").append(System.getProperty("java.version"))
				.append(System.lineSeparator());
		sb.append("Operating System: ").append(System.getProperty("os.name"))
				.append(System.lineSeparator());
		sb.append("Architecture: ").append(System.getProperty("os.arch"))
				.append(System.lineSeparator());
		sb.append("Number of Processors: ").append(
				Runtime.getRuntime().availableProcessors());
		return sb.toString();
	}

	/*
	 * Adds the different sub-results to the meta result
	 */
	private void addResults(PeaksResult result, Iterable<AnalysisResult> results) {
		for (AnalysisResult ar : results) {
			switch (ar.getType()) {
			case DIRECT_NATIVE_USAGE_RESULT:
				result.setDirectNativeUsageResult(ar);
				break;
			case REFLECTION_ANALYSIS_RESULT:
				result.setReflectionUsageResult(ar);
				break;
			case STATIC_USAGE_ANALYSIS_RESULT:
				result.setStaticUsageResult(ar);
				break;
			case TRANSITIVE_NATIVE_USAGE_RESULT:
				result.setTransitveNativeUsageResult(ar);
				break;
			}
		}
	}

	/*
	 * Returns the name of the analysed Jar. On any generic Path this code
	 * Fragment would not work. But we already checked whether it ends in .jar.
	 */
	private String getJarName() {
		String fileName = pathToJar.getFileName().toString();
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
}
