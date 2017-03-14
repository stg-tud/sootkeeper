package de.tud.cs.peaks.toolbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.CRC32;

public class ChecksumGenerator {
    public static long generateChecksumFromPath(String path) {
        File file = new File(path);
        if (file.isDirectory()) throw new IllegalArgumentException("Cannot generate Checksum for directory");
        return new ChecksumGenerator().generateChecksum(file);
    }

    public static long generateChecksumFromPath(Path path){
        File file = path.toFile();
        if (file.isDirectory()) throw new IllegalArgumentException("Cannot generate Checksum for directory");
        return new ChecksumGenerator().generateChecksum(file);

    }


    public long generateChecksum(File file) {
        long result = 0;
        try (FileInputStream is = new FileInputStream(file)){
            byte[] data = new byte[(int) file.length()];
            is.read(data);
            is.close();
            CRC32 crc = new CRC32();
            crc.update(data);
            result = crc.getValue();
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(ChecksumGenerator.class);
            logger.error("There was an error calculating the Checksum",e);
        }
        return result;
    }
}
