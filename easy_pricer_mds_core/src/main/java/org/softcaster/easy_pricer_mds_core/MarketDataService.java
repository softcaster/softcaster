/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.enums.RequestType;
import org.softcaster.engine.curve.YieldCurve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public class MarketDataService {

    @Autowired
    @Qualifier("yieldCurveBuilder")
    private YieldCurveBuilder yieldCurveBuilder;

    @Autowired
    InstrumentQuoteDAO instrumentQuoteDAO;

    @Autowired
    SystemBusinessCalendarDAO systemBusinessCalendarDAO;
    
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

    public double getSpotPrice(Integer masterDataId, RequestType request) {
        double spotPrice = 0.;
        InstrumentQuote iq = instrumentQuoteDAO.findByMasterDataId(masterDataId);
        if (iq != null) {
            spotPrice = getSpotPrice(iq.getCode(), request);
        }
        return spotPrice;
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

    //
    // Aggiornamento Prezzi Spot
    //
    public void updateSpotPrice(List<TokenItem> tokens, Market market) {
        for (TokenItem token : tokens) {
            addSpotPrice(token.symbol(), token.provider(), market);
        }
    }

    public void updateSpotPrice(Map<String, List<String>> tokenList, Market market) {
        try {
            for (Map.Entry<String, List<String>> entry : tokenList.entrySet()) {
                String provider = entry.getKey();
                List<String> tokens = entry.getValue();

                for (String token : tokens) {
                    addSpotPrice(token, provider, market);
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw ex;
        }
    }

    public void loadSpotPrice() {
        // Leggo lista quotazioni
        List<InstrumentQuote> iqList = instrumentQuoteDAO.findAll();
        if (iqList.isEmpty()) {
            return;
        }

        for (InstrumentQuote quote : iqList) {
            updatePrice(quote.getCode(), quote.getBid(), quote.getAsk(), (quote.getBid() + quote.getAsk()) / 2.);
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

        if (newInputs.isEmpty()) {
            return false;
        }
        // compute viene eseguito SEMPRE, sia se la mappa è vuota sia se è piena computeIfPresent 
        // usa una lamba function (->), quando esce existingCurve e' updateCurve
        YieldCurve updatedCurve = this.yieldCurves.compute(curveId, (id, existingCurve) -> {
            if (existingCurve == null) {
                // Se la curva non c'è in cache, la creiamo ex-novo tramite il builder
                return yieldCurveBuilder.buildYieldCurve(id, newInputs, getOfficialDate());
            } else {
                // Se esiste già, sfruttiamo il metodo synchronized esistente
                existingCurve.updateCurve(newInputs);
                return existingCurve;
            }
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

    public YieldCurve getYieldCurve(String curveId) {
        YieldCurve yc = yieldCurves.get(curveId);
        if (yc != null) {
            return yc;
        } else {
            throw new MarketDataNotFoundException("Yield curve " + curveId + " not found");
        }
    }

    public double getYieldCurveRate(String curveName, LocalDate settlement) {
        return yieldCurves.get(curveName).getDiscountFactor(settlement);
    }

    public void saveOrUpdateCurveRates(String curveId) {
        YieldCurve yc = yieldCurves.get(curveId);

        List<CurveNodeInput> newInputs = new ArrayList<>(yc.getAllNodes());
        yieldCurveBuilder.saveOrUpdateCurve(curveId, newInputs);
    }

    public void loadCurveCurveRates(String curveId) {
        List<CurveNodeInput> newInput = yieldCurveBuilder.getNewInput(curveId);
        updateYieldCurveInCache(curveId, newInput);
    }

    // Il risultato viene salvato nella cache chiamata "businessCalendar"
    @Cacheable(value = "businessCalendar")
    public LocalDate getOfficialDate() {
        SystemBusinessCalendar sbc = systemBusinessCalendarDAO.findBySbcId(1);
        if (sbc != null) {
            return sbc.getOfficialDate();
        }
        return null;
    }

    // Metodo da chiamare (o esporre via endpoint/scheduler) quando cambia il giorno sul DB
    @CacheEvict(value = "businessCalendar", allEntries = true)
    public void refreshOfficialDate() {
        // Questo metodo svuota la cache, costringendo la chiamata successiva a getOfficialDate() a rifare la select
        // Nota che non serve implementare nulla nel corpo della funzione
    }
    
    @Cacheable(value = "systemCurrency")
    public Currency getSystemCurrency() {
        SystemBusinessCalendar sbc = systemBusinessCalendarDAO.findBySbcId(1);
        if (sbc != null) {
            return sbc.getCurrency();
        }
        return null;
    }

    @CacheEvict(value = "systemCurrency", allEntries = true)
    public void refreshSystemCurrency() {
    }
}
