/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.csv;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author softc
 */
public class CsvProvider extends AbstractProvider {

    private static CsvProvider _instance = null;
    private String csvFileName = "";
    public static final String IMPORT_PATH = System.getProperty("user.dir") + "/csv/";

    private CsvProvider() {
    }

    public static CsvProvider getInstance() {
        if (_instance == null) {
            _instance = new CsvProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        if (info.getExtraParameters().get(0) != null && !info.getExtraParameters().get(0).isBlank()) {
            csvFileName = info.getExtraParameters().get(0);
        } else {
            csvFileName = "";
        }

    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {

        List<Node> nodes = null;
        if (idCurve != null && !idCurve.isBlank() && !csvFileName.isBlank()) {

            CsvImport csvImport = new CsvImport();
            Path path = Paths.get(IMPORT_PATH + csvFileName);

            ImportConfig config = new ImportConfig();
            config.setSeparator(';');
            config.setFilePath(path);
            config.setStartData(1);
            config.setCharset(StandardCharsets.UTF_8); // utf-8

            try {
                csvImport.startImport(config);
                List<String[]> rows = csvImport.getBuffer();
                int total = rows.size();
                int current = 0;
                for (String[] s : rows) {
                    if (s[0].isEmpty()) {
                        System.out.println("Error: " + s[0].trim());
                        continue;
                    }
                    String alfa3Code = s[4].trim();
                }

            } catch (Exception ex) {
                String error = "Error during import: " + ex.getLocalizedMessage();
                LoggerMgr.logError(error);
             }
        }
        return nodes;
    }
}