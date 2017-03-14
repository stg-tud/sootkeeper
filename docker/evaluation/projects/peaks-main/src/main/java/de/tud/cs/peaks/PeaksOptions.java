package de.tud.cs.peaks;

import de.tud.cs.peaks.sootconfig.*;

public class PeaksOptions {

	public final static FluentOptions standard = new FluentOptions()
			.keepLineNumbers()
			.fullResolver()
			.noBodiesForExcluded()
			.allowPhantomReferences()
			.wholeProgramAnalysis()
			.outputFormat(OutputFormat.NONE)
			.prependClasspath()
			.addPhaseOptions(new CallGraphPhaseOptions().processAllReachable())
			.addPhaseOptions(
					new JimpleBodyCreationPhaseOptions().useOriginalNames())
			.addPhaseOptions(new TagAggregatorOptions().aggregateLineNumber());

	public final static FluentOptions standardPlusCoffi = new FluentOptions()
			.keepLineNumbers()
			.fullResolver()
			.useCoffi()
			.noBodiesForExcluded()
			.allowPhantomReferences()
			.wholeProgramAnalysis()
			.outputFormat(OutputFormat.NONE)
			.prependClasspath()
			.addPhaseOptions(new CallGraphPhaseOptions().processAllReachable())
			.addPhaseOptions(
					new JimpleBodyCreationPhaseOptions().useOriginalNames())
			.addPhaseOptions(new TagAggregatorOptions().aggregateLineNumber());

	public final static FluentOptions standardMinusAllReachable = new FluentOptions()
			.keepLineNumbers()
			.fullResolver()
			.noBodiesForExcluded()
			.allowPhantomReferences()
			.wholeProgramAnalysis()
			.outputFormat(OutputFormat.NONE)
			.prependClasspath()
			.addPhaseOptions(new CallGraphPhaseOptions())
			.addPhaseOptions(
					new JimpleBodyCreationPhaseOptions().useOriginalNames())
			.addPhaseOptions(new TagAggregatorOptions().aggregateLineNumber());

	public final static FluentOptions standardPlusCoffiMinusAllReachable = new FluentOptions()
			.keepLineNumbers()
			.fullResolver()
			.noBodiesForExcluded()
			.allowPhantomReferences()
			.wholeProgramAnalysis()
			.outputFormat(OutputFormat.NONE)
			.prependClasspath()
			.addPhaseOptions(new CallGraphPhaseOptions())
			.addPhaseOptions(
					new JimpleBodyCreationPhaseOptions().useOriginalNames())
			.addPhaseOptions(new TagAggregatorOptions().aggregateLineNumber());
}
