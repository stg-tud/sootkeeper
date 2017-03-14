package de.tud.cs.peaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

public class WorkListRunner {

	private static final float RELEVANT_RATING = 0.1f;

	public static void main(String[] args) throws IOException {
		System.out.println("PEAKS Object-Capability Analysis - "
				+ "Version 0.8");

		if (args.length <= 1) {
			System.out
					.println("Usage: peaks pathOfWorkListFile outputFilePath");
			return;
		}

		FileWriter fw = new FileWriter(args[1]);
		fw.write("path;staticUsage;staticUsageRating;IntrusiveReflection;directNative;directNativeRating;transitiveNative;transitiveNativeRating\n");
		File worklist = new File(args[0]);
		if (worklist.isFile()) {
			List<File> jars = new ArrayList<File>();
			BufferedReader br = new BufferedReader(new FileReader(worklist));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() != 0)
					jars.add(new File(line));
			}
			br.close();
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
}
