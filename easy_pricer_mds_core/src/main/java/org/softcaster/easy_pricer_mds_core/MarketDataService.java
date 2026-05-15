/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.enums.RequestType;
import org.softcaster.engine.curve.YieldCurve;

/**
 *
 * @author softc
 */
public class MarketDataService {

    private static final String IMPORT_PATH = System.getProperty("user.dir") + "/conf";

    private final ConcurrentHashMap<String, SpotPrice> spotPrices = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, YieldCurve> yieldCurves = new ConcurrentHashMap<>();

    public MarketDataService() {

    }

     // Aggiorna l'ultimo prezzo di un asset (es. Forex)
    private void updatePrice(String ticker, double bid, double ask, double middle) {
        spotPrices.put(ticker, new SpotPrice(ticker, bid, ask, middle));
    }

    private void addSpotPrice(String token, String strProvider, Market market) {
        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        if (provider != null) {
            Node node = provider.getMktQuote(token, market);
            double bid = node.getData().bid();
            double ask = node.getData().ask();
            updatePrice(token, bid, bid, (bid + ask) / 2.);
        }
    }
   
    // Recupera il prezzo per il Risk Engine
    private SpotPrice getSpot(String ticker) {
        SpotPrice sp = spotPrices.get(ticker);
        if (sp != null) {
            return sp;
        } else {
            throw new MarketDataNotFoundException("Ticker " + ticker + " not found");
        }
    }

    public double getSpotPrice(String ticker, RequestType request) {
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

    public double getYieldCurveRate(String curveName, LocalDate settlement) {
        return yieldCurves.get(curveName).getDiscountFactor(settlement);
    }
    
    /*
    public NavigableMap<org.softcaster.commons.types.Date, Double> getRates(String idCurve) {
        return yieldCurves.get(idCurve).getRatesMap();
    }
    */
    
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
                addSpotPrice(token, provider, Market.CURRENCIES);
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
                    addSpotPrice(token, provider, Market.FUTURES);
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public void updateBondPrice(Map<String, List<String>> tokenList, IProgressInfo progressInfo) {
        try {
            for (Map.Entry<String, List<String>> entry : tokenList.entrySet()) {
                String provider = entry.getKey();
                List<String> tokens = entry.getValue();

                for (String token : tokens) {
                    addSpotPrice(token, provider, Market.BONDS);
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
                addSpotPrice(token, provider, Market.BONDS);
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

    private void addPrice(String[] line) {
        IMarketDataProvider provider = ProviderFactory.getInstance(line[2]);
        String token = line[1];
        if (provider != null) {
            if (line[3].compareToIgnoreCase("FFU") == 0) {
                token += "#" + line[0];
            }
            addSpotPrice(token, line[2], Market.BONDS);
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
                addYieldCurve(s[2]);
            }

        } catch (Exception ex) {
            String error = "Error reading file: ycurve_link.csv [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        }
    }

    private void addYieldCurve(String strProvider) {

        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        if (provider != null) {
            switch (strProvider) {
                case "EuriborRatesProvider" -> {
                }
                case "Sole24hProvider" -> {
                }
                default -> {
                }
            }
        }
    }

    public void updateYieldCurve(String strProvider,String idCurve) {
        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        List<Node>  nodes = provider.getYieldCurveNodes(idCurve);
        //yieldCurves.put(idCurve, new YieldCurve("USD", "USD", nodes));
    }
}
