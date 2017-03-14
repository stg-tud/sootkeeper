package de.tud.cs.peaks.results;

public enum AnalysisType {
    DIRECT_NATIVE_USAGE_RESULT("DirectNativeUsageResult"),
    REFLECTION_ANALYSIS_RESULT("ReflectionAnalysisResult"),
    STATIC_USAGE_ANALYSIS_RESULT("StaticUsageAnalysisResult"),
    TRANSITIVE_NATIVE_USAGE_RESULT("TransitiveNativeUsageResult");
    private final String description;

    private AnalysisType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
