/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.cme.Csv;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;

public class Csv implements MarketDataManager {

    public static final String IMPORT_PATH = System.getProperty("user.dir") + "/csv/";

    public Csv(RequestParamManager rpm) {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    private void parseRow(String[] s, List<Node> rates) {
        Node node = getNode(s);
        rates.add(node);
    }

    private Node getNode(String[] row) {
        try {
            Offset offset = new Offset(Converter.toInt(row[1]), OffsetType.fromId(Converter.toInt(row[2])));
            Data data = new Data(Converter.toDouble(row[6], false) / 100., Converter.toDouble(row[7], false) / 100.);
            String symbol = row[0];
            return new Node(symbol,
                    offset,
                    DaycountBasis.fromId(Converter.toInt(row[3])),
                    Compounding.fromId(Converter.toInt(row[4])),
                    CurveNodeType.fromId(Converter.toInt(row[5])),
                    data
            );
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public List<Node> getRates(String curveId) {
        if (curveId != null && !curveId.isBlank()) {

            CsvImport csvImport = new CsvImport();
            Path path = Paths.get(IMPORT_PATH + curveId + ".csv");

            ImportConfig config = new ImportConfig();
            config.setSeparator(';');
            config.setFilePath(path);
            config.setStartData(3);
            config.setCharset(StandardCharsets.UTF_8); // utf-8

            try {
                List<Node> rates = new ArrayList<>();
                csvImport.startImport(config);
                List<String[]> rows = csvImport.getBuffer();
                int current = 0;
                for (String[] s : rows) {
                    if (current == 0) {
                        current++;
                    } else {
                        parseRow(s, rates);
                        current++;
                    }
                }
                return rates;
            } catch (Exception ex) {
                String error = "Error during import: " + ex.getLocalizedMessage();
                LoggerMgr.logError(error);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String getMarketDataServiceId() {
        return "Csv";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.RATES);
        return markets;
    }
}
