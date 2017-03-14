package de.tud.cs.peaks.osgi.peaks.analysis.directNativeUsage.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.results.AnalysisResult;

public class DirectNativeResult implements IAnalysisResult {
    AnalysisResult result;

    public AnalysisResult getResult() {
        return result;
    }

    public DirectNativeResult(AnalysisResult result) {
        this.result = result;
    }
}
