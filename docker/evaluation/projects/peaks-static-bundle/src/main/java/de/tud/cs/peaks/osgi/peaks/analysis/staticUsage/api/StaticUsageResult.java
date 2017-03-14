package de.tud.cs.peaks.osgi.peaks.analysis.staticUsage.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.results.AnalysisResult;

public class StaticUsageResult implements IAnalysisResult {
    AnalysisResult result;

    public AnalysisResult getResult() {
        return result;
    }

    public StaticUsageResult(AnalysisResult result) {
        this.result = result;
    }
}
