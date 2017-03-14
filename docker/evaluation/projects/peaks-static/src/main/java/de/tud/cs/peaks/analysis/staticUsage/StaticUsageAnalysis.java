package de.tud.cs.peaks.analysis.staticUsage;

import static de.tud.cs.peaks.results.AnalysisType.STATIC_USAGE_ANALYSIS_RESULT;
import static de.tud.cs.peaks.toolbox.tags.PeaksTag.sideEffectTag;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.util.Chain;
import de.tud.cs.peaks.CallGraphAnalysis;
import de.tud.cs.peaks.misc.PeaksField;
import de.tud.cs.peaks.misc.Visibility;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.analysis.staticUsage.toolbox.ImmutableCheck;
import de.tud.cs.peaks.analysis.staticUsage.toolbox.tags.SideEffectTag;

public class StaticUsageAnalysis extends CallGraphAnalysis {
	private static final String VERSION = "0.8";// ;)
	private static final Logger logger = LoggerFactory
			.getLogger(StaticUsageAnalysis.class);
	/*
	 * If we at any time want to actually analyse and report JCL classes we need
	 * to set this
	 */
	private static final boolean ANALYSEJDK = false;

	private AnalysisResult staticUsageAnalysisResult;

	private CallGraph callGraph;
	private ImmutableCheck immutableCheck;

	public static boolean isSideEffectFree(SootMethod sootMethod, boolean first) {
		if (!sootMethod.hasTag(sideEffectTag)) {
			if (sootMethod.hasActiveBody()) {
				SideEffectAnalysis sideEffectAnalysis = new SideEffectAnalysis(
						sootMethod, first);
				sideEffectAnalysis.doAnalyis();
				// TODO: Why is this case distinction needed? Code is redundant.
			} else if (sootMethod.isNative() || sootMethod.isAbstract()
					|| sootMethod.isPhantom()) {
				logger.debug("Fallback " + sootMethod.getDeclaration() + " in "
						+ sootMethod.getDeclaringClass().getName());

				sootMethod.addTag(new SideEffectTag(false));
			} else {
				logger.debug("Fallback " + sootMethod.getDeclaration() + " in "
						+ sootMethod.getDeclaringClass().getName());

				sootMethod.addTag(new SideEffectTag(false));
			}
		}
		SideEffectTag tag = (SideEffectTag) sootMethod.getTag(sideEffectTag);
		return tag.isSideEffectFree();
	}

	@Override
	public AnalysisResult performAnalysis(CallGraph cg) {

		staticUsageAnalysisResult = new AnalysisResult(VERSION,
				STATIC_USAGE_ANALYSIS_RESULT);
		callGraph = cg;
		Chain<SootClass> classes = Scene.v().getClasses();
		removeLibraryClasses(classes);
		immutableCheck = new ImmutableCheck();
		for (SootClass currentClass : classes) {
			fieldAnalysis(currentClass);
		}

		return staticUsageAnalysisResult;

	}

	private void removeLibraryClasses(Chain<SootClass> classes) {
		if (!ANALYSEJDK) {
			Iterator<SootClass> classIt = classes.iterator();
			while (classIt.hasNext()) {
				SootClass sc = classIt.next();
				if (sc.isJavaLibraryClass()) {
					classIt.remove();
				}
			}
		}
	}

	/**
	 * @param sootClass
	 */
	private void fieldAnalysis(SootClass sootClass) {
		for (SootField field : sootClass.getFields()) {
			if (field.isStatic()) {
				if (immutableCheck.isImmutable(field)) {
					staticUsageAnalysisResult
							.addImmutableField(createFieldAbstraction(field,
									0.0f));
				} else {
					if (field.isPrivate()) {
						staticUsageAnalysisResult
								.addField(createFieldAbstraction(field, 0.5f));
					}

					staticUsageAnalysisResult.addField(createFieldAbstraction(
							field, 1.0f));
				}
			}
		}
	}

	private PeaksField createFieldAbstraction(SootField field, float rating) {
		Visibility visibility = getVisibility(field.getModifiers());
		return new PeaksField(field.getName(), visibility, field
				.getDeclaringClass().getName(), field.getType().toString(),
				rating);
	}
}
