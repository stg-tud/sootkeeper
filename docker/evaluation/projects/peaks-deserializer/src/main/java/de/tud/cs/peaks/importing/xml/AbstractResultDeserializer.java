package de.tud.cs.peaks.importing.xml;

import nu.xom.Builder;
import nu.xom.Element;
import nu.xom.ParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractResultDeserializer {
	protected final Logger logger = LoggerFactory.getLogger("Deserializer");
	protected Element root;

	public AbstractResultDeserializer(String xml) throws ParsingException,
			IOException {
		Builder builder = new Builder(false);
		root = builder.build(xml, null).getRootElement();
		parseResult();
	}

	public AbstractResultDeserializer(File xml) throws IOException,
			ParsingException {
		Builder builder = new Builder(false);
		root = builder.build(xml).getRootElement();
		parseResult();
	}

	public AbstractResultDeserializer(InputStream xml) throws ParsingException,
			IOException {
		Builder builder = new Builder(false);
		root = builder.build(xml).getRootElement();
		parseResult();
	}

	protected abstract void parseResult();
}
