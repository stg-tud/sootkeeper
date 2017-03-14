package de.tud.cs.peaks;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import de.tud.cs.peaks.analysis.directNativeUsage.DirectNativeUsage;
import de.tud.cs.peaks.analysis.reflectionUsage.ReflectionAnalysis;
import de.tud.cs.peaks.analysis.staticUsage.StaticUsageAnalysis;
import de.tud.cs.peaks.analysis.transitiveNativeUsage.TransitiveNativeUsage;
import de.tud.cs.peaks.exporting.Exporter;
import de.tud.cs.peaks.exporting.html.HTMLExporter;
import de.tud.cs.peaks.results.PeaksResult;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;

public class Runner {

	private static final CallGraphAnalysis[] toRun = {
			new StaticUsageAnalysis(), new DirectNativeUsage(),
			new TransitiveNativeUsage(), new ReflectionAnalysis() };

	public static void main(String[] args) {

		System.out.println("PEAKS Object-Capability Analysis - "
				+ "Version 0.8");

		if (args.length <= 0) {
			System.out.println("Usage: peaks libraryToAnalyze.jar");
			return;
		}

		for (AnalysisTarget t : Collections.singleton(new AnalysisTarget()
				.processPath(args[0]).classPathToProcessPathDirectory())) {

			System.out.println("Processing " + t.toString() + " ...");

			PeaksResult result = new SingleJarAnalysis(Arrays.asList(toRun),
					t.getProcessPath()).analyse(PeaksOptions.standard
					.includeAll());

			Exporter e = new HTMLExporter();
			String constructedPathName = constructExportPathName(t);
			e.export(result, constructedPathName);

			System.out.println("Opening results...");
			try {
				String os = System.getProperty("os.name");
				if (os.contains("Win") || os.contains("win")) {
					Runtime.getRuntime().exec(
							"cmd /c start " + constructedPathName
									+ "/result.html");
				} else if (os.contains("Linux")) {
					Runtime.getRuntime()
							.exec("xdg-open " + constructedPathName
									+ "/result.html").waitFor();
				} else {
					Runtime.getRuntime().exec(
							"open " + constructedPathName + "/result.html");
				}

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

	}

	private static String constructExportPathName(AnalysisTarget t) {
		return "results/peaks-" + new File(t.getProcessPath()).getName()
				+ ".html";
	}
}
