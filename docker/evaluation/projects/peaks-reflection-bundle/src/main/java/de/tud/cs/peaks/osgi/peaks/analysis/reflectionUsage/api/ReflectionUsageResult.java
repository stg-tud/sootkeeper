package de.tud.cs.peaks.osgi.peaks.analysis.reflectionUsage.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.results.AnalysisResult;

public class ReflectionUsageResult implements IAnalysisResult {
    AnalysisResult result;

    public AnalysisResult getResult() {
        return result;
    }

    public ReflectionUsageResult(AnalysisResult result) {
        this.result = result;
    }
}
