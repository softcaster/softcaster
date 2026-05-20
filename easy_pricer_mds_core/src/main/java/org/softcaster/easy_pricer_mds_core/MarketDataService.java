/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.enums.RequestType;
import org.softcaster.engine.curve.YieldCurve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author softc
 */
public class MarketDataService {

    @Autowired
    @Qualifier("yieldCurveBuilder")
    private YieldCurveBuilder yieldCurveBuilder;

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
            updatePrice(token, bid, ask, (bid + ask) / 2.);
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

                if (progressInfo != null) {
                    progressInfo.setProgress(progress + step * cnt);
                    cnt++;
                }

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

    public void updateBondPrice(List<String> tokenList, IProgressInfo progressInfo) {
        Map<String, List<String>> localMap = new HashMap<>();
        localMap.put("EuroNextProvider", tokenList);
        updateBondPrice(localMap, progressInfo);
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
            throw ex;
        }
    }

    //
    // YieldCurve
    //
    public void addYieldCurve(String strProvider, String idCurve) {
        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        if (provider != null && idCurve != null && !idCurve.isBlank()) {
            YieldCurve yc = yieldCurveBuilder.buildYieldCurve(provider, idCurve, LocalDate.now());
            yieldCurves.put(idCurve, yc);
        }
    }

    /**
     * Aggiorna una curva di rendimento esistente nella cache con i nuovi dati
     * dal provider.
     *
     * @param curveId Identificativo univoco della curva (es. "EUR_OIS")
     * @param newInputs La nuova lista di nodi aggiornati ricevuta dal provider
     * @return true se la curva è stata aggiornata, false se non era presente
     * nella cache
     */
    public boolean updateYieldCurveInCache(String curveId, List<CurveNodeInput> newInputs) {
        if (curveId == null || newInputs == null) {
            throw new IllegalArgumentException("L'ID curva e i nuovi input non possono essere nulli.");
        }

        // Specifichiamo esplicitamente il tipo atomico restituito dalla mappa
        // che computeIfPresent usa una lamba function (->), quando esce existingCurve e' 
        // updateCurve
        YieldCurve updatedCurve = this.yieldCurves.computeIfPresent(curveId, (id, existingCurve) -> {
            // Sfrutta il metodo synchronized creato dentro YieldCurve
            existingCurve.updateCurve(newInputs);
            return existingCurve;
        });

        // Se updatedCurve non è null significa che la curva esisteva ed è stata modificata
        return updatedCurve != null;
    }

    public void updateYieldCurve(String strProvider, String curveId) {
        IMarketDataProvider provider = ProviderFactory.getInstance(strProvider);
        if (provider != null && curveId != null && !curveId.isBlank()) {
            List<CurveNodeInput> newInput = yieldCurveBuilder.getNewInput(provider, curveId);
            updateYieldCurveInCache(curveId, newInput);
        }
    }
}
