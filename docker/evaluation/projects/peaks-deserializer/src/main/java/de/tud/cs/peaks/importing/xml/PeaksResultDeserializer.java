package de.tud.cs.peaks.importing.xml;

import de.tud.cs.peaks.results.PeaksResult;
import nu.xom.ParsingException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.*;

public class PeaksResultDeserializer extends AbstractResultDeserializer {
	private PeaksResult result;

	public PeaksResultDeserializer(String xml) throws ParsingException,
			IOException {
		super(xml);
	}

	public PeaksResultDeserializer(File xml) throws IOException,
			ParsingException {
		super(xml);
	}

	public PeaksResultDeserializer(InputStream xml) throws ParsingException,
			IOException {
		super(xml);
	}

	@Override
	protected void parseResult() {
		String dateText = root.getAttributeValue(ANALYSIS_DATE_ATTRIBUTE);
		Date date = new Date(Long.valueOf(dateText));
		String machine = root.getAttributeValue(ANALYSIS_MACHINE_ATTRIBUTE);
		String durationText = root
				.getAttributeValue(ANALYSIS_DURATION_ATTRIBUTE);
		long duration = Long.valueOf(durationText);
		String jarName = root.getAttributeValue(JAR_NAME_ATTRIBUTE);
		result = new PeaksResult(duration, machine, jarName, date);
	}

	public PeaksResult getResult() {
		return result;
	}
}
