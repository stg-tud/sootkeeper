package de.tud.cs.peaks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.tud.cs.peaks.sootconfig.AnalysisTarget;

public class PeaksTargets {

	public final static AnalysisTarget log4j_1_2_17 = new AnalysisTarget()
			.processPath("../peaks-libraries/log4j/log4j-1.2.17.jar");

	public final static AnalysisTarget lwjgl_2_8_1 = new AnalysisTarget()
			.processPath("../peaks-libraries/lwjgl/lwjgl-2.8.1/jar/lwjgl.jar");

	public final static AnalysisTarget hibernate_4_2_3 = new AnalysisTarget()
			.processPath(
					"../peaks-libraries/hibernate/hibernate-core-4.2.3.Final.jar")
			.classPathToProcessPathDirectory();

	public final static AnalysisTarget domination = new AnalysisTarget()
			.processPath("../peaks-libraries/Domination/Domination.jar");

	public final static AnalysisTarget openjdk_rt_v1_7_0_u19 = new AnalysisTarget()
			.processPath(
					"../peaks-libraries/jre/openjdk/openjdk_1.7.0_19/rt.jar")
			.classPathToProcessPathDirectory();

	public final static List<AnalysisTarget> all = new ArrayList<AnalysisTarget>();

	static {
		Collections.addAll(all, log4j_1_2_17, lwjgl_2_8_1, hibernate_4_2_3,
				domination);
	}
}
