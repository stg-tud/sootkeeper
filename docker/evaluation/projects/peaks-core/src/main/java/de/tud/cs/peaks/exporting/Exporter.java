package de.tud.cs.peaks.exporting;

import de.tud.cs.peaks.results.PeaksResult;

public interface Exporter {
	
	public void export(PeaksResult result, String path);

}
