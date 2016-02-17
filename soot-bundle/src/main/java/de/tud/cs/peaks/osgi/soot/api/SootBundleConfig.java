package de.tud.cs.peaks.osgi.soot.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisConfig;
import de.tud.cs.peaks.sootconfig.AnalysisTarget;
import de.tud.cs.peaks.sootconfig.FluentOptions;

public class SootBundleConfig extends IAnalysisConfig {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SootBundleConfig that = (SootBundleConfig) o;

        if (!fluentOptions.equals(that.fluentOptions)) return false;
        return analysisTarget.equals(that.analysisTarget);

    }

    @Override
    public int hashCode() {
        int result = fluentOptions.hashCode();
        result = 31 * result + analysisTarget.hashCode();
        return result;
    }
}
