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
import java.util.function.Function;
import java.util.stream.Collectors;
import org.softcaster.core.data.SpreadCurveEntity;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.core.data.YieldCurveItem;
import org.softcaster.core.data.AbstractYieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.softcaster.engine.curve.CurveBootstrapper;
import org.softcaster.engine.curve.CurveNode;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.MarketQuote;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.SpreadProfile;
import org.softcaster.engine.curve.SpreadedCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.CurveKind;
import org.softcaster.engine.enums.OffsetType;
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

    public CurveKind getCurveKind(String code) {
        boolean spread = spreadCurveEntityDAO.existsByCode(code);
        boolean plain = yieldCurveEntityDAO.existsByCode(code);
        if (spread && plain) {
            throw new IllegalStateException("Curve code defined in both yield_curve and spread_curve: " + code);
        }
        if (!spread && !plain) {
            throw new IllegalArgumentException("Unknown curve: " + code);
        }
        return spread ? CurveKind.SPREAD : CurveKind.YIELD;
    }

    public DiscountCurve buildSpreadCurve(String code, LocalDate officialDate,
            Function<String, YieldCurve> yieldCurveResolver) {
        SpreadCurveEntity sc = spreadCurveEntityDAO.findByCodeWithBase(code);
        if (sc == null) {
            throw new IllegalArgumentException("Unknown spread curve: " + code);
        }
        final String baseCode = sc.getBaseCurve().getCode();

        YieldCurve base = yieldCurveResolver.apply(baseCode);
        if (!base.getValuationDate().equals(officialDate)) {
            // spreads are expressed on the time scale of the official date: base and spreads must share it
            throw new IllegalStateException("Base curve " + baseCode + " is dated " + base.getValuationDate()
                    + ", expected " + officialDate);
        }

        List<? extends AbstractYieldCurveSpread> rows = yieldCurveSpreadDAO.findLatest(sc.getIdSpreadCurve(), officialDate);
        if (rows.isEmpty()) {
            throw new IllegalStateException("No z-spreads for " + code + " on or before " + officialDate);
        }

        LocalDate asOf = rows.get(0).getAsOfDate();
        long ageDays = ChronoUnit.DAYS.between(asOf, officialDate);          // calendar days
        if (ageDays > sc.getMaxAgeDays()) {
            throw new IllegalStateException("Z-spreads for " + code + " are stale: as of " + asOf
                    + " (" + ageDays + " days, max " + sc.getMaxAgeDays() + ")");
        }

        Map<Offset, Double> buckets = new LinkedHashMap<>();
        for (AbstractYieldCurveSpread r : rows) {
            if (!baseCode.equals(r.getBaseCurveCode())) {
                throw new IllegalStateException("Z-spreads for " + code + " were calibrated on "
                        + r.getBaseCurveCode() + ", but the configured base curve is " + baseCode);
            }
            OffsetType type = OffsetType.fromId(r.getOffsetType());           // ADAPT: IdentifiableEnum lookup
            buckets.put(new Offset(r.getOffsetValue(), type), r.getzSpread());
        }
        // YieldCurve.addOffset must be public static: SpreadProfile.of uses it
        return new SpreadedCurve(base, SpreadProfile.of(officialDate, buckets));
    }

    public SpreadedCurve buildSpreadCurve(SpreadCurveEntity dbCurve, LocalDate officialDate) {
        List<? extends AbstractYieldCurveSpread> rows = yieldCurveSpreadDAO.findLatest(dbCurve.getIdSpreadCurve(), officialDate);
        if (rows.isEmpty()) {
            throw new IllegalStateException("Curve " + dbCurve.getCode() + " requires z-spreads but none found on or before " + officialDate);
        }
        LocalDate asOf = rows.get(0).getAsOfDate();
        if (ChronoUnit.DAYS.between(asOf, officialDate) > dbCurve.getMaxAgeDays()) {
            throw new IllegalStateException("Z-spreads for " + dbCurve.getCode() + " are stale: as of " + asOf);
        }

        Map<Offset, Double> buckets = new LinkedHashMap<>();
        for (AbstractYieldCurveSpread r : rows) {
            buckets.put(new Offset(r.getOffsetValue(), OffsetType.YEARS), r.getzSpread());
        }

        YieldCurveEntity baseCurve = yieldCurveEntityDAO.findByCode(dbCurve.getBaseCurve().getCode());
        return new SpreadedCurve(buildYieldCurve(baseCurve, officialDate), SpreadProfile.of(officialDate, buckets));
    }

    public YieldCurve buildYieldCurve(YieldCurveEntity dbCurve, LocalDate officialDate) {
        if (dbCurve != null) {
            Currency currency = Currency.getInstance(dbCurve.getCurrency().getIsoCode());
            List<CurveNode> nodes = null;
            List<MarketQuote> marketQuotes = withFillCurve(dbCurve, getMarketQuotes(dbCurve));
            if (marketQuotes != null && !marketQuotes.isEmpty()) {
                nodes = CurveBootstrapper.bootstrap(officialDate, marketQuotes);
            }
            return org.softcaster.engine.curve.YieldCurve.fromDiscountFactors(officialDate, currency, nodes);
        } else {
            return null;
        }
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
            List<CurveNode> nodes = CurveBootstrapper.bootstrap(officialDate, withFillCurve(dbCurve, newInputs));
            return org.softcaster.engine.curve.YieldCurve.fromDiscountFactors(officialDate, currency, nodes);
        } else {
            return null;
        }
    }

    /**
     * Nodi della curva dai suoi input (es. appena scaricati dal provider),
     * completati con la curva di supporto.
     */
    public List<CurveNode> bootstrap(String curveCode, List<MarketQuote> quotes, LocalDate officialDate) {
        return CurveBootstrapper.bootstrap(officialDate, withFillCurve(yieldCurveEntityDAO.findByCode(curveCode), quotes));
    }

    // simbolo dei nodi presi dalla curva di supporto: <curva di supporto>/<ric>
    private static final String FILL_SEPARATOR = "/";

    /**
     * Completa gli input di una curva con la sua curva di supporto
     * (yield_curve.fill_curve): si aggiungono i nodi della curva di supporto
     * con scadenza precedente al primo swap / OIS della curva e non gia'
     * presenti (es. SOFR_OIS da Cbonds: depositi fino a 3M e OIS dal 3Y; da
     * TERMSOFR entrano 6M e 1Y). Un deposito a un anno entra come OIS a un
     * anno (stesso tasso semplice, un solo pagamento): il bootstrap non usa i
     * depositi dal primo nodo annuale in poi. I nodi presi hanno simbolo
     * <curva di supporto>/<ric> e non si salvano sulla curva
     * (saveOrUpdateCurve).
     */
    public List<MarketQuote> withFillCurve(YieldCurveEntity dbCurve, List<MarketQuote> quotes) {
        if (dbCurve == null || dbCurve.getFillCurve() == null || quotes == null || quotes.isEmpty()) {
            return quotes;
        }
        String fillCode = yieldCurveEntityDAO.getCodeFromId(dbCurve.getFillCurve());
        YieldCurveEntity fill = fillCode == null ? null : yieldCurveEntityDAO.findByCode(fillCode);
        List<MarketQuote> fillQuotes = getMarketQuotes(fill);
        if (fillQuotes == null || fillQuotes.isEmpty()) {
            return quotes;
        }
        // primo swap / OIS della curva, in mesi
        MarketQuote firstLong = quotes.stream()
                .filter(q -> q.nodeType() == org.softcaster.engine.enums.CurveNodeType.OIS
                || q.nodeType() == org.softcaster.engine.enums.CurveNodeType.SWAP)
                .min(java.util.Comparator.comparingInt(YieldCurveBuilder::tenorDays)).orElse(null);
        int limitDays = firstLong == null ? Integer.MAX_VALUE : tenorDays(firstLong);
        boolean ois = firstLong != null && firstLong.nodeType() == org.softcaster.engine.enums.CurveNodeType.OIS;
        java.util.Set<Integer> present = quotes.stream().map(YieldCurveBuilder::tenorDays)
                .collect(Collectors.toCollection(java.util.HashSet::new));

        List<MarketQuote> merged = new ArrayList<>(quotes);
        for (MarketQuote f : fillQuotes) {
            int days = tenorDays(f);
            if (days >= limitDays || present.contains(days)) {
                continue;
            }
            org.softcaster.engine.enums.CurveNodeType type = f.nodeType();
            org.softcaster.engine.enums.Compounding compounding = f.compounding();
            if (ois && type == org.softcaster.engine.enums.CurveNodeType.MONEY_MARKET && days == 365
                    && f.daycount() == firstLong.daycount()) {
                type = org.softcaster.engine.enums.CurveNodeType.OIS;
                compounding = org.softcaster.engine.enums.Compounding.COMPOUNDED;
            }
            merged.add(new MarketQuote(fillCode + FILL_SEPARATOR + f.symbol(), f.tenorOffset(), f.rate(), f.daycount(),
                    compounding, type));
            present.add(days);
        }
        return merged;
    }

    /**
     * Scadenza indicativa in giorni per confrontare i tenor (1Y = 365, 6M =
     * 180, 1W = 7): non serve alle date, solo all'ordinamento e ai doppioni.
     */
    private static int tenorDays(MarketQuote q) {
        Offset o = q.tenorOffset();
        return switch (o.offsetType()) {
            case DAYS -> (int) o.step();
            case MONTHS -> o.step() % 12 == 0 ? (int) (o.step() / 12 * 365) : (int) (o.step() * 30);
            case YEARS -> (int) (o.step() * 365);
            default -> Integer.MAX_VALUE;
        };
    }

    private MarketQuote getMarketQuote(YieldCurveItem item) {

        OffsetType offsetType = OffsetType.fromId(item.getOffsetType());
        Offset offset = new Offset(item.getOffsetValue(), offsetType);
        MarketQuote marketQuote = new MarketQuote(item.getRic(), offset, item.getBid(),
                item.getDaycount(), item.getCompounding(), item.getNodeType());
        return marketQuote;
    }
/*
    private MarketQuote getMarketQuote(org.softcaster.market_data.core.Node node) {
        MarketQuote marketQuote = null;
        OffsetType offsetType = OffsetType.fromCode(node.offset().offsetType().getCode());
        long step = node.offset().step();
        marketQuote = new MarketQuote(node.symbol(),
                new Offset(step, offsetType),
                node.data().bid(),
                node.daycount(),
                node.compounding(),
                node.nodeType());
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
*/
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
                // i nodi della curva di supporto restano su quella curva
                if (node.mq() != null && !node.mq().symbol().contains(FILL_SEPARATOR)) {
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

    public List<String> getDiscountCurveNames() {
        List<String> names = yieldCurveEntityDAO.findNames();
        names.addAll(spreadCurveEntityDAO.findNames());
        return names;
    }
}
