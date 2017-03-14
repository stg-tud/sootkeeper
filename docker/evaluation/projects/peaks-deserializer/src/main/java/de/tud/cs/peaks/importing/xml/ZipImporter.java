package de.tud.cs.peaks.importing.xml;

import de.tud.cs.peaks.importing.Importer;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.PeaksResult;
import nu.xom.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.META_RESULT_NAME;

public class ZipImporter implements Importer {

    private static final Logger logger = LoggerFactory.getLogger(ZipImporter.class);

	private ZipImporter() {
	}

	public static PeaksResult deserializeZip(ZipFile file) {
		LinkedList<AnalysisResult> results = new LinkedList<>();
		Enumeration<? extends ZipEntry> entries = file.entries();
		PeaksResult result = null;
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.endsWith(".xml")) {
				if (name.startsWith(META_RESULT_NAME)) {
					result = deserializeMetaResult(file, entry);
				} else {
					results.add(deserializeResult(file, entry));
				}
			}
		}
		combineResults(results, result);
		return result;
	}

	private static void combineResults(LinkedList<AnalysisResult> results,
			PeaksResult result) {
		for (AnalysisResult res : results) {
			switch (res.getType()) {
			case DIRECT_NATIVE_USAGE_RESULT:
				result.setDirectNativeUsageResult(res);
				break;
			case REFLECTION_ANALYSIS_RESULT:
				result.setReflectionUsageResult(res);
				break;
			case STATIC_USAGE_ANALYSIS_RESULT:
				result.setStaticUsageResult(res);
				break;
			case TRANSITIVE_NATIVE_USAGE_RESULT:
				result.setTransitveNativeUsageResult(res);
				break;
			}
		}
	}

	private static AnalysisResult deserializeResult(ZipFile file, ZipEntry entry) {
		try {
			return new ResultDeserializer(file.getInputStream(entry))
					.getResult();
		} catch (IOException e) {
                logger.error("Could not open " + file);
                throw new RuntimeException("IOException",e);
		} catch (ParsingException e) {
			    logger.error("Could not parse " + entry);
                throw new RuntimeException("ParseException",e);
		}
	}

	private static PeaksResult deserializeMetaResult(ZipFile file, ZipEntry entry) {
        PeaksResult result = null;
		try {
			PeaksResultDeserializer rd = new PeaksResultDeserializer(
					file.getInputStream(entry));
			result = rd.getResult();
		} catch (ParsingException | IOException e) {
			throw new RuntimeException("Could not parse Meta Result", e);
		}
		return result;
	}

}
