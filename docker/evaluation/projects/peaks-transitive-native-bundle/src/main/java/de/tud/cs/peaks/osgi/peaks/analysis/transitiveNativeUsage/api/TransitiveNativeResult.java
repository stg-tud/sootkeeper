package de.tud.cs.peaks.osgi.peaks.analysis.transitiveNativeUsage.api;

import de.tud.cs.peaks.osgi.framework.api.data.IAnalysisResult;
import de.tud.cs.peaks.results.AnalysisResult;

public class TransitiveNativeResult implements IAnalysisResult {
    AnalysisResult result;

    public AnalysisResult getResult() {
        return result;
    }

    public TransitiveNativeResult(AnalysisResult result) {
        this.result = result;
    }
}
