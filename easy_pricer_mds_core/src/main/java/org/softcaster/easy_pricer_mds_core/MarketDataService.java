/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.marketdataprovider.CmeGroup.CmeGroupProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.IMarketDataProvider;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.REQUEST_TYPE;
import static org.softcaster.marketdataprovider.REQUEST_TYPE.ASK;
import static org.softcaster.marketdataprovider.REQUEST_TYPE.BID;
import static org.softcaster.marketdataprovider.REQUEST_TYPE.MIDDLE;
import org.softcaster.marketdataprovider.YieldNode;

/**
 *
 * @author softc
 */
public class MarketDataService {

    private static final String IMPORT_PATH = System.getProperty("user.dir") + "/conf";
    private static MarketDataService _instance = null;

    private final ConcurrentHashMap<String, SpotPrice> spotPrices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, YieldCurve> yieldCurves = new ConcurrentHashMap<>();

    private MarketDataService() {

    }

    public static MarketDataService getInstance() {
        if (_instance == null) {
            _instance = new MarketDataService();
        }

        return _instance;
    }

    // Recupera il prezzo per il Risk Engine
    public SpotPrice getSpot(String ticker) {
        SpotPrice sp = spotPrices.get(ticker);
        if (sp != null) {
            return sp;
        } else {
            throw new MarketDataNotFoundException("Ticker " + ticker + " not found");
        }
    }

    public double getSpotPrice(String ticker, REQUEST_TYPE request) {
        double price = 0.;
        try {
            switch (request) {
                case BID ->
                    price = getSpot(ticker).bid();
                case ASK ->
                    price = getSpot(ticker).ask();
                case MIDDLE ->
                    price = getSpot(ticker).middle();
                default -> {
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            price = 0;
        }
        return price;
    }

    public YieldCurve getYieldCurve(String curveName) {
        YieldCurve yc = yieldCurves.get(curveName);
        if (yc != null) {
            return yc;
        } else {
            throw new MarketDataNotFoundException("Yield curve " + curveName + " not found");
        }
    }

    public double getYieldCurveRate(String curveName, org.softcaster.commons.types.Date settlement) {
        return yieldCurves.get(curveName).getRate(settlement);
    }

    // Metodo per il refresh dei dati richiesti passati in input
    public void updateMarketData() {
        // Refresh prezzi spot titoli,cambi,futures ET
        updateSpotPrice();

        // Refresh yield curves
        updateYieldCurves();
    }

    //
    // SpotPrice
    //
    private void updateSpotPrice() {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/ticker_link.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            for (String[] s : csvImport.getBuffer()) {
                addPrice(s);
            }

        } catch (Exception ex) {
            String error = "Error reading file: ticker_link.csv [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        }
    }

    public void updateFxPrice(List<String> tokenList, String provider, IProgressInfo progressInfo) {

        int progress = tokenList.size() / 10;
        if (progress < 10) {
            progress = tokenList.size();
        } else {
            progress = 100 / progress;
        }

        int step = 100 / progress;
        int cnt = 0;
        try {
            // Legge tutto in un unico blocco
            for (String token : tokenList) {
                addSpotPrice(token, provider, MARKETS.CURRENCIES);
                break;
                /*
                if (progressInfo != null) {
                    progressInfo.setProgress(progress + step * cnt);
                    cnt++;
                }
                 */
            }
            if (progressInfo != null) {
                progressInfo.setProgress(100);
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public void updateBondFutPrice(Map<String, List<String>> tokenList, IProgressInfo progressInfo) {

        try {
            for (Map.Entry<String, List<String>> entry : tokenList.entrySet()) {
                String provider = entry.getKey();
                List<String> tokens = entry.getValue();

                for (String token : tokens) {
                    addSpotPrice(token, provider, MARKETS.FUTURES);
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public void updateBondPrice(Map<String, List<String>> tokenList, Object object) {
        try {
            for (Map.Entry<String, List<String>> entry : tokenList.entrySet()) {
                String provider = entry.getKey();
                List<String> tokens = entry.getValue();

                for (String token : tokens) {
                    addSpotPrice(token, provider, MARKETS.BONDS);
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public void updateSecurityPrice(IProgressInfo progressInfo) {
        // Leggo lista di currency pairs
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String securities = paramsMgr.getParamValue("SECURITIES");
        String provider = paramsMgr.getParamValue("SECURITIES_PROVIDER");

        // 1. Divido la stringa in un array usando la virgola come separatore
        String[] tokenArray = securities.split(",");
        // 2. Converto l'array in una List<String>
        List<String> tokenList = Arrays.asList(tokenArray);

        int progress = tokenList.size() / 10;
        if (progress < 10) {
            progress = tokenList.size();
        } else {
            progress = 100 / progress;
        }

        int step = 100 / progress;
        int cnt = 0;
        try {
            for (String token : tokenList) {
                addSpotPrice(token, provider, MARKETS.BONDS);
                if (progressInfo != null) {
                    progressInfo.setProgress(progress + step * cnt);
                    cnt++;
                }
            }
            progressInfo.setProgress(100);
        } catch (Exception ex) {
            String error = "Error reading file: ticker_link.csv [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        }
    }

    // Aggiorna l'ultimo prezzo di un asset (es. Forex)
    private void updatePrice(String ticker, double bid, double ask, double middle) {
        spotPrices.put(ticker, new SpotPrice(ticker, bid, ask, middle));
    }

    private void addSpotPrice(String token, String strProvider, MARKETS market) {
        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        if (provider != null) {
            switch (strProvider) {
                case "EuroNextProvider" -> {
                    ConnectionParam param = new ConnectionParam();
                    param.baseUrl = "https://live.euronext.com/en/";
                    param.url = "";
                    param.extraParams.add(token);
                    param.extraParams.add(""); //-DMIL future -ETLX equity
                    param.market = market;

                    provider.refresh(param);
                    List<DataNode> rates = provider.quotes(market);
                    for (DataNode node : rates) {
                        if (node.getRic().equalsIgnoreCase(token)) {
                            updatePrice(token, node.getBid(), node.getAsk(), (node.getBid() + node.getAsk()) / 2.);
                            break;
                        }
                    }
                }

                case "CmeGroupProvider" -> {
                    provider = CmeGroupProvider.getInstance();
                    ConnectionParam param = new ConnectionParam();
                    param = new ConnectionParam();
                    param.baseUrl = "https://www.cmegroup.com";
                    param.url = "https://www.cmegroup.com/CmeWS/mvc/quotes/v2/";
                    // parametri
                    param.extraParams.add(token);
                    param.market = MARKETS.FUTURES;
                    provider.refresh(param);

                    List<DataNode> rates = provider.quotes(MARKETS.FUTURES);
                    for (DataNode node : rates) {
                        String[] tokens = token.split("@");
                        updatePrice(tokens[0] + "@" + node.getRic(), node.getBid(), node.getAsk(), (node.getBid() + node.getAsk()) / 2.);
                    }
                }

                case "InvestingComProvider" -> {
                    ConnectionParam param = new ConnectionParam();
                    param.baseUrl = "https://www.investing.com";
                    param.url = "https://www.investing.com/currencies/streaming-forex-rates-majors";
                    param.market = MARKETS.CURRENCIES;
                    provider.refresh(param);

                    // Questo provider legge tutto in un blocco
                    List<DataNode> rates = provider.quotes(MARKETS.CURRENCIES);
                    for (DataNode node : rates) {
                        updatePrice(node.getRic(), node.getBid(), node.getAsk(), (node.getBid() + node.getAsk()) / 2.);
                    }
                }
                default -> {
                }
            }
        }
    }

    private void addPrice(String[] line) {
        IMarketDataProvider provider = ProviderFactory.getInstance(line[2]);
        String token = line[1];
        if (provider != null) {
            if (line[3].compareToIgnoreCase("FFU") == 0) {
                token += "#" + line[0];
            }
            addSpotPrice(token, line[2], MARKETS.BONDS);
        }
    }

    //
    // YieldCurve
    //
    private void updateYieldCurves() {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/ycurve_link.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        try {
            csvImport.startImport(config);
            for (String[] s : csvImport.getBuffer()) {
                addYieldCurve(s);
            }

        } catch (Exception ex) {
            String error = "Error reading file: ycurve_link.csv [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        }
    }

    private void addYieldCurve(String[] line) {

        IMarketDataProvider provider = ProviderFactory.getInstance(line[2]);
        if (provider != null) {
            switch (line[2]) {
                case "EuriborRatesProvider" -> {
                    ConnectionParam param = new ConnectionParam();
                    param.baseUrl = "https://www.euribor-rates.eu/it/";
                    param.url = "https://www.euribor-rates.eu/it/tassi-euribor-aggiornati/";
                    param.extraParams.add(line[0]);
                    param.market = MARKETS.YIELDS;
                    provider.refresh(param);

                    List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
                    NavigableMap<org.softcaster.commons.types.Date, Double> sourceRates = new TreeMap<>();
                    for (DataNode node : rates) {
                        if (node instanceof YieldNode yieldNode) {
                            sourceRates.put(yieldNode.getMaturity(), node.getBid());
                        }
                    }
                    yieldCurves.put(line[0], new YieldCurve(line[0], line[1], sourceRates));
                }
                case "Sole24hProvider" -> {
                    ConnectionParam param = new ConnectionParam();
                    param.baseUrl = "https://www.ilsole24ore.com/";
                    param.url = "https://mercatiwdg.ilsole24ore.com/FinanzaMercati/WidgetSelector/listino?widgetConfiguration=FMIRS";
                    param.extraParams.add(line[0]);
                    param.market = MARKETS.YIELDS;
                    provider.refresh(param);

                    List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
                    NavigableMap<org.softcaster.commons.types.Date, Double> sourceRates = new TreeMap<>();
                    for (DataNode node : rates) {
                        if (node instanceof YieldNode yieldNode) {
                            sourceRates.put(yieldNode.getMaturity(), node.getBid());
                        }
                    }
                    yieldCurves.put(line[0], new YieldCurve(line[0], line[1], sourceRates));
                }
                default -> {
                }
            }
        }
    }

    public NavigableMap<org.softcaster.commons.types.Date, Double> getRates(String idCurve) {
        return yieldCurves.get(idCurve).getRatesMap();
    }

    public void updateYieldCurve(String idCurve) {
        switch (idCurve) {
            case "SOFR" ->
                updateSofrTem();
            default -> {
            }
        }
    }

    private void updateSofrTem() {
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.cmegroup.com";
        param.url = "https://www.cmegroup.com/services/sofr-strip-rates/";
        param.extraParams.add("SOFR");
        param.market = MARKETS.YIELDS;

        CmeGroupProvider provider = CmeGroupProvider.getInstance();
        provider.refresh(param);

        List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
        NavigableMap<org.softcaster.commons.types.Date, Double> sourceRates = new TreeMap<>();
        for (DataNode node : rates) {
            if (node instanceof YieldNode yieldNode) {
                sourceRates.put(yieldNode.getMaturity(), node.getBid());
            }
        }
        yieldCurves.put("SOFR", new YieldCurve("USD", "USD", sourceRates));
    }
}
