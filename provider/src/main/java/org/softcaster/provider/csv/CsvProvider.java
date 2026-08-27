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
import java.text.ParseException;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.Offset;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.enums.OffsetType;

/**
 *
 * @author softc
 */
public class CsvProvider extends AbstractProvider {

    private static CsvProvider _instance = null;
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
    public List<Node> getYieldCurveNodes(String idCurve) {

        RateKey key = new RateKey(idCurve, RATES);
        if (idCurve != null && !idCurve.isBlank()) {

            CsvImport csvImport = new CsvImport();
            Path path = Paths.get(IMPORT_PATH + idCurve + ".csv");

            ImportConfig config = new ImportConfig();
            config.setSeparator(';');
            config.setFilePath(path);
            config.setStartData(1);
            config.setCharset(StandardCharsets.UTF_8); // utf-8

            try {
                csvImport.startImport(config);
                List<String[]> rows = csvImport.getBuffer();
                int current = 0;
                for (String[] s : rows) {
                    if (current == 0) {
                        current++;
                    } else {
                        parseRow(s, key);
                        current++;
                    }
                }

            } catch (Exception ex) {
                String error = "Error during import: " + ex.getLocalizedMessage();
                LoggerMgr.logError(error);
            }
        }
        return getRates(key);
    }

    private void parseRow(String[] s, RateKey key) {
        Node node = getNode(s);
        addRate(key, node);
    }

    private Node getNode(String[] s) {
        try {
            Offset offset = new Offset(Converter.toInt(s[2]), getOffsetType(Converter.toInt(s[1])));
            Data data = new Data(Converter.toDouble(s[3], false) / 100., Converter.toDouble(s[4], false) / 100.);
            String ric = s[0];
            return new Node(ric, offset, data, getDaycount(Converter.toInt(s[6])), getCompounding(Converter.toInt(s[5])));
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    String getDaycount(Integer daycount) {
        String value = "";
        switch (daycount) {
            case 1 ->
                value = "NASD_30_360";
            case 2 ->
                value = "ACT_360";
            case 3 ->
                value = "ACT_365";
            case 4 ->
                value = "ACT_ACT_ISDA";
            case 5 ->
                value = "ACT_ACT_ICMA";
            case 6 ->
                value = "EUR_30_360";
            default -> {
            }
        }

        return value;
    }

    String getCompounding(Integer compounding) {
        String value = "";
        switch (compounding) {
            case 1 ->
                value = "SIMPLE";
            case 2 ->
                value = "COMPOUNDED";
            case 3 ->
                value = "SIMPLE_THEN_COMPOUNDED";
            case 4 ->
                value = "CONTINUOUS";
            default -> {
            }
        }

        return value;
    }

    private OffsetType getOffsetType(Integer type) {
        OffsetType offsetType = OffsetType.NONE;

        switch (type) {
            case 1 ->
                offsetType = OffsetType.DAYS;
            case 2 ->
                offsetType = OffsetType.MONTHS;
            case 3 ->
                offsetType = OffsetType.YEARS;
            default -> {
            }
        }
        return offsetType;
    }
}
