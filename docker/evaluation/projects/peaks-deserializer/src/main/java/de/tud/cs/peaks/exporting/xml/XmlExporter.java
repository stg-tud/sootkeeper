package de.tud.cs.peaks.exporting.xml;

import de.tud.cs.peaks.exporting.Exporter;
import de.tud.cs.peaks.results.AnalysisResult;
import de.tud.cs.peaks.results.PeaksResult;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static de.tud.cs.peaks.importing.xml.SerializationConstants.META_RESULT_NAME;

public class XmlExporter implements Exporter {

    @Override
    public void export(PeaksResult result, String path) {
        File folder = new File(path);
        if (!folder.exists()&&!folder.mkdirs()){
             LoggerFactory.getLogger(getClass()).error("Could not create Output folders");
            return;
        }
        File zip = new File(path + File.separator + result.getJarName() + ".zip");
        try (FileOutputStream fileStream = new FileOutputStream(zip);
             ZipOutputStream zipStream = new ZipOutputStream(fileStream)) {
            for (AnalysisResult ar : result.getAllResults()) {
                if (ar != null) {
                    addResultToZip(zipStream, ar);
                }
            }
            ZipEntry entry = new ZipEntry(META_RESULT_NAME+".xml");
            zipStream.putNextEntry(entry);
            new PeaksResultSerializer(result).printToStream(zipStream);
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("There was an error saving the results.", e);
        }
    }

    private void addResultToZip(ZipOutputStream zipStream, AnalysisResult result)
            throws IOException {
        ZipEntry entry = new ZipEntry(result.getType().getDescription()
                + ".xml");
        zipStream.putNextEntry(entry);
        ResultSerializer serializer = new ResultSerializer(result);
        serializer.printToStream(zipStream);
    }
}
