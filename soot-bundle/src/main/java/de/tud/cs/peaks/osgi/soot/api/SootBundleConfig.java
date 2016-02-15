package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;

public class SootBundleConfig implements IAnalysisConfig {
	private final FluentOptions fluentOptions;
    private final AnalysisTarget analysisTarget;

    public SootBundleConfig(FluentOptions fluentOptions, AnalysisTarget analysisTarget) {
        this.fluentOptions = fluentOptions;
        this.analysisTarget = analysisTarget;
    }

    public FluentOptions getFluentOptions() {
        return fluentOptions;
    }

    public AnalysisTarget getAnalysisTarget() {
        return analysisTarget;
    }
}
