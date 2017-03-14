package de.tud.cs.peaks.results;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PeaksResult {
	// Meta data
	private final long analysisDuration;
	private final String analysisMachine;
	private final String jarName;
	private final Date date;
	// Anaylsis Results
	private AnalysisResult directNativeUsageResult;
	private AnalysisResult transitveNativeUsageResult;
	private AnalysisResult reflectionUsageResult;
	private AnalysisResult staticUsageResult;

	public PeaksResult(long analysisDuration, String analysisMachine,
			String jarName) {
		super();
		this.analysisDuration = analysisDuration;
		this.analysisMachine = analysisMachine;
		this.jarName = jarName;
		this.date = new Date();
	}

    public PeaksResult(long analysisDuration, String analysisMachine, String jarName, Date date) {
        this.analysisDuration = analysisDuration;
        this.analysisMachine = analysisMachine;
        this.jarName = jarName;
        this.date = date;
    }

    public AnalysisResult getDirectNativeUsageResult() {
		return directNativeUsageResult;
	}

	public void setDirectNativeUsageResult(
			AnalysisResult directNativeUsageResult) {
		this.directNativeUsageResult = directNativeUsageResult;
	}

	public AnalysisResult getTransitveNativeUsageResult() {
		return transitveNativeUsageResult;
	}

	public void setTransitveNativeUsageResult(
			AnalysisResult transitveNativeUsageResult) {
		this.transitveNativeUsageResult = transitveNativeUsageResult;
	}

	public AnalysisResult getReflectionUsageResult() {
		return reflectionUsageResult;
	}

	public void setReflectionUsageResult(AnalysisResult reflectionUsageResult) {
		this.reflectionUsageResult = reflectionUsageResult;
	}

	public AnalysisResult getStaticUsageResult() {
		return staticUsageResult;
	}

	public void setStaticUsageResult(AnalysisResult staticUsageResult) {
		this.staticUsageResult = staticUsageResult;
	}

	public long getAnalysisDuration() {
		return analysisDuration;
	}

	public String getAnalysisMachine() {
		return analysisMachine;
	}

	public String getJarName() {
		return jarName;
	}

	public Date getDate() {
		return date;
	}

	public Set<AnalysisResult> getAllResults() {
		HashSet<AnalysisResult> results = new HashSet<>();
		if (directNativeUsageResult != null)
			results.add(directNativeUsageResult);
		if (staticUsageResult != null)
			results.add(staticUsageResult);
		if (reflectionUsageResult != null)
			results.add(reflectionUsageResult);
		if (transitveNativeUsageResult != null)
			results.add(transitveNativeUsageResult);
		return results;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PeaksResult result = (PeaksResult) o;

        if (analysisDuration != result.analysisDuration) return false;
        if (!analysisMachine.equals(result.analysisMachine)) return false;
        if (!date.equals(result.date)) return false;
        if (directNativeUsageResult != null ? !directNativeUsageResult.equals(result.directNativeUsageResult) : result.directNativeUsageResult != null)
            return false;
        if (!jarName.equals(result.jarName)) return false;
        if (reflectionUsageResult != null ? !reflectionUsageResult.equals(result.reflectionUsageResult) : result.reflectionUsageResult != null)
            return false;
        if (staticUsageResult != null ? !staticUsageResult.equals(result.staticUsageResult) : result.staticUsageResult != null)
            return false;
        if (transitveNativeUsageResult != null ? !transitveNativeUsageResult.equals(result.transitveNativeUsageResult) : result.transitveNativeUsageResult != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (analysisDuration ^ (analysisDuration >>> 32));
        result = 31 * result + analysisMachine.hashCode();
        result = 31 * result + jarName.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + (directNativeUsageResult != null ? directNativeUsageResult.hashCode() : 0);
        result = 31 * result + (transitveNativeUsageResult != null ? transitveNativeUsageResult.hashCode() : 0);
        result = 31 * result + (reflectionUsageResult != null ? reflectionUsageResult.hashCode() : 0);
        result = 31 * result + (staticUsageResult != null ? staticUsageResult.hashCode() : 0);
        return result;
    }
}
