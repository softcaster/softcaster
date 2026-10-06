/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.softcaster.core.data.SpreadCurveEntity;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.core.data.YieldCurveItem;
import org.softcaster.core.data.YieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.softcaster.engine.curve.CurveBootstrapper;
import org.softcaster.engine.curve.CurveNode;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.MarketQuote;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.SpreadProfile;
import org.softcaster.engine.curve.SpreadedCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.bricks.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("yieldCurveBuilder")
public class YieldCurveBuilder {

    @Autowired
    YieldCurveEntityDAO yieldCurveEntityDAO;
    @Autowired
    SpreadCurveEntityDAO spreadCurveEntityDAO;

    @Autowired
    YieldCurveSpreadDAO yieldCurveSpreadDAO;

    public YieldCurve buildYieldCurve(YieldCurveEntity dbCurve, LocalDate officialDate) {
        if (dbCurve != null) {
            List<MarketQuote> marketQuotes = getMarketQuotes(dbCurve);
            Currency currency = Currency.getInstance(dbCurve.getCurrency().getIsoCode());
            List<CurveNode> nodes = CurveBootstrapper.bootstrap(officialDate, marketQuotes);
            return org.softcaster.engine.curve.YieldCurve.fromDiscountFactors(officialDate, currency, nodes);
        } else {
            return null;
        }
    }
    
    public SpreadedCurve buildSpreadCurve(SpreadCurveEntity dbCurve, LocalDate officialDate) {
        List<YieldCurveSpread> rows = yieldCurveSpreadDAO.findLatest(dbCurve.getIdSpreadCurve(), officialDate);
        if (rows.isEmpty()) {
            throw new IllegalStateException("Curve " + dbCurve.getCode() + " requires z-spreads but none found on or before " + officialDate);
        }
        LocalDate asOf = rows.get(0).getAsOfDate();
        if (ChronoUnit.DAYS.between(asOf, officialDate) > dbCurve.getMaxAgeDays()) {
            throw new IllegalStateException("Z-spreads for " + dbCurve.getCode() + " are stale: as of " + asOf);
        }
        String expectedBase = dbCurve.getBaseCurve().getProvider();                          // or the field that holds the base curve code
        for (YieldCurveSpread r : rows) {
            if (!expectedBase.equals(r.getBaseCurveCode())) {
                throw new IllegalStateException("Z-spreads calibrated on " + r.getBaseCurveCode() + ", expected " + expectedBase);
            }
        }

        Map<Offset, Double> buckets = new LinkedHashMap<>();
        for (YieldCurveSpread r : rows) {
            buckets.put(new Offset(r.getOffsetValue(), OffsetType.YEARS), r.getzSpread());
        }
        return new SpreadedCurve(buildYieldCurve(dbCurve.getBaseCurve(), officialDate), SpreadProfile.of(officialDate, buckets));
    }

    public DiscountCurve buildDiscountCurve(String code, LocalDate officialDate) {

        SpreadCurveEntity sc = spreadCurveEntityDAO.findByCodeWithBase(code);
        YieldCurveEntity yc = yieldCurveEntityDAO.findByCode(code);
        if (sc != null && yc != null) {
            throw new IllegalStateException("Curve code defined in both yield_curve and spread_curve: " + code);
        }

        if (sc != null) {
            return buildSpreadCurve(sc, officialDate);
        }
        if (yc != null) {
            return buildYieldCurve(yc, officialDate);
        }
        throw new IllegalArgumentException("Unknown curve: " + code);
    }

    public org.softcaster.engine.curve.YieldCurve buildYieldCurve(String idCurve, List<MarketQuote> newInputs, LocalDate officialDate) {
        YieldCurveEntity dbCurve = yieldCurveEntityDAO.findByCode(idCurve);
        if (dbCurve != null) {
            Currency currency = Currency.getInstance(dbCurve.getCurrency().getIsoCode());
            List<CurveNode> nodes = CurveBootstrapper.bootstrap(officialDate, newInputs);
            return org.softcaster.engine.curve.YieldCurve.fromDiscountFactors(officialDate, currency, nodes);
        } else {
            return null;
        }
    }

    private MarketQuote getMarketQuote(YieldCurveItem item) {

        OffsetType offsetType = OffsetType.fromId(item.getOffsetType());
        Offset offset = new Offset(item.getOffsetValue(), offsetType);
        MarketQuote marketQuote = new MarketQuote(item.getRic(), offset, item.getBid(),
                item.getDaycount(), item.getCompounding(), item.getNodeType());
        return marketQuote;
    }

    private MarketQuote getMarketQuote(Node node) {
        MarketQuote marketQuote = null;
        OffsetType offsetType = OffsetType.fromCode(node.getOffset().offsetType().getCode());
        long step = node.getOffset().step();
        marketQuote = new MarketQuote(node.getSymbol(), new Offset(step, offsetType), node.getData().bid(),
                DaycountBasis.fromCode(node.getDaycount()),
                Compounding.fromCode(node.getCompounding()),
                CurveNodeType.fromCode(node.getNodeType()));
        return marketQuote;
    }

    public List<MarketQuote> getNewInput(IMarketDataProvider provider, String curveId) {
        List<MarketQuote> newInput = null;
        List<Node> nodes = provider.getYieldCurveNodes(curveId);
        if (nodes != null && !nodes.isEmpty()) {
            newInput = new ArrayList<>();
            MarketQuote marketQuote;
            for (Node n : nodes) {
                marketQuote = getMarketQuote(n);
                if (marketQuote != null) {
                    newInput.add(marketQuote);
                }
            }
        }
        return newInput;
    }

    public List<MarketQuote> getNewInput(String curveId) {
        List<MarketQuote> newInput = null;
        // Recupero yield curve
        YieldCurveEntity dbCurve = yieldCurveEntityDAO.findByCode(curveId);
        if (dbCurve != null && dbCurve.getItems() != null) {
            List<YieldCurveItem> existingDbItems = dbCurve.getItems();
            newInput = new ArrayList<>();
            MarketQuote marketQuote;
            for (YieldCurveItem item : existingDbItems) {
                marketQuote = getMarketQuote(item);
                if (marketQuote != null) {
                    newInput.add(marketQuote);
                }
            }
        }
        return newInput;
    }

    public List<MarketQuote> getMarketQuotes(YieldCurveEntity dbCurve) {
        List<MarketQuote> newInput = null;
        // Recupero yield curve
        if (dbCurve != null && dbCurve.getItems() != null) {
            List<YieldCurveItem> existingDbItems = dbCurve.getItems();
            newInput = new ArrayList<>();
            MarketQuote marketQuote;
            for (YieldCurveItem item : existingDbItems) {
                marketQuote = getMarketQuote(item);
                if (marketQuote != null) {
                    newInput.add(marketQuote);
                }
            }
        }
        return newInput;
    }

    public void saveOrUpdateCurve(String curveId, List<CurveNode> newInputs) {

        if (newInputs == null || newInputs.isEmpty()) {
            return;
        }

        // Recupero yield curve
        YieldCurveEntity dbCurve = yieldCurveEntityDAO.findByCode(curveId);
        if (dbCurve != null && dbCurve.getItems() != null) {

            // Mappa gli item attualmente presenti sul DB per una ricerca veloce O(1)
            // Usiamo come chiave la combinazione "step_code" (es. "3_MONTHS")
            Map<String, YieldCurveItem> existingDbItems = dbCurve.getItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getRic(),
                            item -> item
                    ));

            List<YieldCurveItem> updatedItems = new ArrayList<>();

            // 3. Allinea i dati finanziari con le entità DB
            for (CurveNode node : newInputs) {
                if (node.mq() != null) {
                    String key = node.mq().symbol();
                    if (existingDbItems.containsKey(key)) {
                        // Il nodo esiste già a DB: aggiorna solo tasso e discount factor (UPDATE)
                        YieldCurveItem existingItem = existingDbItems.get(key);
                        existingItem.setAsk(node.mq().rate());
                        existingItem.setBid(node.mq().rate());
                        updatedItems.add(existingItem);
                    } else {
                        // Il nodo è nuovo: crea una nuova entità (INSERT)
                        YieldCurveItem newItem = new YieldCurveItem();
                        newItem.setRic(node.mq().symbol());
                        newItem.setYieldCurve(dbCurve.getIdYieldCurve());
                        newItem.setOffsetValue((short) node.mq().tenorOffset().step());
                        newItem.setOffsetType((short) node.mq().tenorOffset().offsetType().getId());
                        newItem.setAsk(node.mq().rate());
                        newItem.setBid(node.mq().rate());
                        newItem.setDaycount(node.mq().daycount());
                        newItem.setCompounding(node.mq().compounding());
                        newItem.setNodeType(node.mq().nodeType());
                        updatedItems.add(newItem);
                    }
                }
            }

            // 4. Applica la lista aggiornata per gestire le eventuali cancellazioni (Orphan Removal)
            dbCurve.getItems().clear();
            dbCurve.getItems().addAll(updatedItems);
            yieldCurveEntityDAO.saveOrUpdate(dbCurve);
        }

    }
}
