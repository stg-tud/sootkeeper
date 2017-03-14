package de.tud.cs.peaks.exporting.xml;

import de.tud.cs.peaks.results.PeaksResult;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import java.io.IOException;
import java.io.OutputStream;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.*;

public class PeaksResultSerializer {
    private final Element root;

    public PeaksResultSerializer(PeaksResult peaksResult) {
        root = new Element(META_RESULT_NAME);
        root.addAttribute(new Attribute(ANALYSIS_DURATION_ATTRIBUTE, String.valueOf(peaksResult.getAnalysisDuration())));
        root.addAttribute(new Attribute(ANALYSIS_MACHINE_ATTRIBUTE, peaksResult.getAnalysisMachine()));
        root.addAttribute(new Attribute(ANALYSIS_DATE_ATTRIBUTE,String.valueOf(peaksResult.getDate().getTime())));
        root.addAttribute(new Attribute(JAR_NAME_ATTRIBUTE, peaksResult.getJarName()));
    }

    public void printToStream(OutputStream stream) throws IOException {
        Serializer ser = new Serializer(stream);
        ser.setIndent(4);
        ser.setMaxLength(64);
        ser.write(new Document(root));
    }

    public final String toXML() {
        return root.toXML();
    }
}
