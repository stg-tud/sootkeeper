package de.tud.cs.peaks;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tud.cs.peaks.analysis.directNativeUsage.DirectNativeUsage;
import de.tud.cs.peaks.analysis.reflectionUsage.ReflectionAnalysis;
import de.tud.cs.peaks.analysis.staticUsage.StaticUsageAnalysis;
import de.tud.cs.peaks.analysis.transitiveNativeUsage.TransitiveNativeUsage;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.PeaksResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;

public class DirectoryRunner {

	private static final float RELEVANT_RATING = 0.1f;

	public static void main(String[] args) throws IOException {

		FileWriter fw = new FileWriter("report.csv");
		fw.write("path;staticUsage;staticUsageRating;IntrusiveReflection;directNative;directNativeRating;transitiveNative;transitiveNativeRating\n");

		System.out.println("PEAKS Object-Capability Analysis - "
				+ "Version 0.8");

		if (args.length <= 0) {
			System.out.println("Usage: peaks directoryToAnalyze");
			fw.close();
			return;
		}

		File dir = new File(args[0]);
		if (dir.isDirectory()) {
			List<File> jars = getAllJars(dir, new ArrayList<File>());
			for (File jar : jars) {
				try {
					CallGraphAnalysis[] toRun = { new StaticUsageAnalysis(),
							new DirectNativeUsage(),
							new TransitiveNativeUsage(),
							new ReflectionAnalysis() };

					AnalysisTarget t = new AnalysisTarget().processPath(
							jar.getAbsolutePath())
							.classPathToProcessPathDirectory();

					System.out.println("Processing " + t.getProcessPath()
							+ " ...");
					PeaksResult result = null;
					try {
						result = new SingleJarAnalysis(Arrays.asList(toRun),
								t.getProcessPath())
								.analyse(PeaksOptions.standard.includeAll());
					} catch (Exception e) {
						try {
							result = new SingleJarAnalysis(
									Arrays.asList(toRun), t.getProcessPath())
									.analyse(PeaksOptions.standardMinusAllReachable
											.includeAll());
						} catch (Exception e1) {
							try {
								result = new SingleJarAnalysis(
										Arrays.asList(toRun),
										t.getProcessPath())
										.analyse(PeaksOptions.standardPlusCoffi
												.includeAll());
							} catch (Exception e2) {
								try {
									result = new SingleJarAnalysis(
											Arrays.asList(toRun),
											t.getProcessPath())
											.analyse(PeaksOptions.standardPlusCoffiMinusAllReachable
													.includeAll());
								} catch (Exception e3) {
									fw.write(t.getProcessPath()
											+ ";defect;defect;defect;defect\n");
									fw.flush();
								}
							}

						}
					}

					StringBuilder reflection = new StringBuilder();
					StringBuilder dnative = new StringBuilder();
					StringBuilder tnative = new StringBuilder();
					StringBuilder staticu = new StringBuilder();

					for (AnalysisResult ar : result.getAllResults())
						switch (ar.getType()) {
						case REFLECTION_ANALYSIS_RESULT:
							reflection.append(ar.getPaths().size());
							break;
						case DIRECT_NATIVE_USAGE_RESULT:
							dnative.append(ar.getRelevantMethods(
									RELEVANT_RATING).size());
							dnative.append(";");
							dnative.append(ar
									.getSumOfRelevantMethods(RELEVANT_RATING));
							break;
						case TRANSITIVE_NATIVE_USAGE_RESULT:
							tnative.append(ar.getRelevantMethods(
									RELEVANT_RATING).size());
							tnative.append(";");
							tnative.append(ar
									.getSumOfRelevantMethods(RELEVANT_RATING));
							break;
						case STATIC_USAGE_ANALYSIS_RESULT:
							staticu.append(ar
									.getRelevantFields(RELEVANT_RATING).size());
							staticu.append(";");
							staticu.append(ar
									.getSumOfRelevantFields(RELEVANT_RATING));
							break;
						}
					StringBuilder output = new StringBuilder();
					output.append(t.getProcessPath());
					output.append(";");
					output.append(staticu);
					output.append(";");
					output.append(reflection);
					output.append(";");
					output.append(dnative);
					output.append(";");
					output.append(tnative);
					output.append("\n");
					fw.write(output.toString());
					fw.flush();

				} catch (Exception e) {
					System.err.println("ERROR with: " + jar.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}

		fw.close();

	}

	private static List<File> getAllJars(File dir, List<File> jars) {
		File[] jarFiles = dir.listFiles(new FilenameFilter() {
			protected final String regex = ".*\\.jar$";

			@Override
			public boolean accept(File dir, String name) {
				return name.matches(regex);
			}

		});
		for (File jar : jarFiles)
			jars.add(jar);
		File[] dirs = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File paramFile) {
				return paramFile.isDirectory();
			}

		});
		for (File subDir : dirs)
			getAllJars(subDir, jars);
		return jars;
	}
}
