/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.imports;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;

/**
 *
 * @author ep
 */
public class CsvImport {

    private List<String[]> buffer = null;
    private int startData = 0;

    public void startImport(ImportConfig config) throws Exception {
        setBuffer(readAllLines(config));
        startData = config.getStartData();
    }

    /**
     * @return the buffer
     */
    public List<String[]> getBuffer() {
        return buffer;
    }

    /**
     * @param buffer the buffer to set
     */
    public void setBuffer(List<String[]> buffer) {
        this.buffer = buffer;
    }

    public String[] getLine(int pos) {
        if (buffer != null) {
            if (pos < startData) {
                pos = startData;
            }
            return buffer.get(pos);
        } else {
            return null;
        }
    }

    private List<String[]> readAllLines(ImportConfig config) throws Exception {
        try (Reader reader = Files.newBufferedReader(config.getFilePath(), config.getCharset())) {

            CSVParser parser = new CSVParserBuilder()
                    .withSeparator(config.getSeparator())
                    .withIgnoreQuotations(true)
                    .build();

            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build();

            return csvReader.readAll();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

}
