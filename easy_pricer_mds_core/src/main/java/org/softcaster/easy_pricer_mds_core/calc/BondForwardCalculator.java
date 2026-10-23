/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.DeliverableBonds;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingResponse;
import org.softcaster.engine.analytics.XRBForwardPricer;
import org.softcaster.engine.curve.CurveNode;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.DeliverableBond;
import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.dto.XRBForwardResult;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;

/**
 * Costruisce l'input del BondForwardPricer per un bond future (es. Mini BTP
 * 10y): carica la scadenza del future (BondFutureMasterData) con testata e paniere
 * consegnabile, prezzi a pronti dei
 * titoli, curva repo, e calcola le date di regolamento.
 *
 * Creato come @Bean in MdsCoreAutoConfiguration: nessuna annotazione @Service.
 *
 * Errori: - NoSuchElementException -> future o curva non trovati (REST: 404) -
 * IllegalArgumentException -> dati non validi (REST: 400) -
 * IllegalStateException -> nessun titolo del paniere utilizzabile
 */
public class BondForwardCalculator {

    private static final Logger log = LoggerFactory.getLogger(BondForwardCalculator.class);

    /**
     * Convenzione repo EUR.
     */
    private static final DaycountBasis REPO_DAYCOUNT = DaycountBasis.ACT_360;

    private final BondFutureMasterDataDAO bondFutureMasterDataDAO;
    private final SecurityMasterDataDAO smdDAO;
    private final InstrumentQuoteDAO instrumentQuoteDAO;
    private final MarketDataService marketDataService;
    private final XRBForwardPricer bondForwardPricer;

    public BondForwardCalculator(BondFutureMasterDataDAO bondFutureMasterDataDAO,
            SecurityMasterDataDAO smdDAO,
            InstrumentQuoteDAO instrumentQuoteDAO,
            MarketDataService marketDataService,
            XRBForwardPricer bondForwardPricer) {
        this.bondFutureMasterDataDAO = Objects.requireNonNull(bondFutureMasterDataDAO);
        this.smdDAO = Objects.requireNonNull(smdDAO);
        this.instrumentQuoteDAO = Objects.requireNonNull(instrumentQuoteDAO);
        this.marketDataService = Objects.requireNonNull(marketDataService);
        this.bondForwardPricer = Objects.requireNonNull(bondForwardPricer);
    }

    // ------------------------------------------------------------------------
    // -- Costruzione input
    // ------------------------------------------------------------------------
    /**
     * Input con la curva repo recuperata dal MarketDataService.
     *
     * @param futureIsin codice o ISIN della scadenza del future (BondFutureMasterData)
     * @param officialDate data di riferimento (oggi)
     * @param repoCurveCode codice della curva repo
     * @param quotedFuturePrice prezzo quotato del future, oppure null per il
     * solo prezzo teorico
     * @return
     */
    public XRBForwardInput getBondForwardInput(String futureIsin, LocalDate officialDate,
            String repoCurveCode, Double quotedFuturePrice) {
        Basket basket = loadBasket(futureIsin, officialDate);
        DiscountCurve repoCurve = loadCurve(repoCurveCode, basket.valuationDate());
        return toInput(futureIsin, basket, repoCurve, quotedFuturePrice);
    }

    public XRBForwardInput getBondForwardInput(String futureIsin, LocalDate officialDate, Double quotedFuturePrice) {
        Basket basket = loadBasket(futureIsin, officialDate);
        DiscountCurve repoCurve = loadCurve("", basket.valuationDate());
        return toInput(futureIsin, basket, repoCurve, quotedFuturePrice);
    }

    /**
     * Input con un tasso repo singolo (DECIMALE, interesse semplice ACT/360),
     * trasformato in una curva a un nodo alla deliveryDate.
     *
     * @param futureIsin
     * @param officialDate
     * @param repoRate
     * @param quotedFuturePrice
     * @return
     */
    public XRBForwardInput getBondForwardInput(String futureIsin, LocalDate officialDate,
            double repoRate, Double quotedFuturePrice) {
        Basket basket = loadBasket(futureIsin, officialDate);
        DiscountCurve repoCurve = singleRateCurve(repoRate, basket);
        return toInput(futureIsin, basket, repoCurve, quotedFuturePrice);
    }

    // ------------------------------------------------------------------------
    // -- Valutazione
    // ------------------------------------------------------------------------
    public XRBForwardResult bondFwdValuation(XRBForwardInput input) {
        return bondForwardPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    /**
     * Entry point REST: per ora la request porta un tasso repo singolo
     * (domesticRate).
     *
     * @param request
     * @return
     */
    public ForwardPricingResponse bondFwdValuation(ForwardPricingRequest request) {
        Objects.requireNonNull(request, "request");
        Double quoted = request.referencePrice > 0 ? request.referencePrice : null;

        XRBForwardInput input = getBondForwardInput(request.isin, request.referenceDate.toLocalDate(),
                request.domesticRate, quoted);
        XRBForwardResult result = bondFwdValuation(input);

        ForwardPricingResponse response = new ForwardPricingResponse();
        response.theoreticalPrice = result.theoreticalPrice();
        response.ctd = result.ctdIsin();
        response.ctdYield = result.ctd().yield();
        response.ctdModifiedDuration = result.ctd().modifiedDuration();
        response.ctdDv01 = result.ctd().dv01();
        response.futureDv01 = result.futureDv01();
        // Se ForwardPricingResponse ha (o avra') i campi, si possono esporre anche:
        // result.termRepoRate(), result.ctd().netBasis(), result.ctd().impliedRepo(), result.deliverables()
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------
    /**
     * Paniere e date, indipendenti dalla curva.
     */
    private record Basket(org.softcaster.core.data.Currency currency, LocalDate valuationDate, LocalDate deliveryDate,
            List<DeliverableBond> deliverables) {
    }

    private Basket loadBasket(String futureIsin, LocalDate officialDate) {
        Objects.requireNonNull(officialDate, "officialDate");
        BondFutureMasterData bfmd = bondFutureMasterDataDAO.getByCodeOrIsin(futureIsin);
        Calendar calendar = new Calendar(bfmd.getCurrency());

        // expiry day della scadenza
        // (due giorni di borsa prima del 10 del mese di contratto per il MBTP).
        LocalDate expiryDate = bfmd.getLastTradingLocalDate();
        if (expiryDate == null) {
            throw new IllegalArgumentException("Ultimo giorno di negoziazione mancante per " + bfmd.getCode());
        }

        List<DeliverableBond> deliverables = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        Integer settlementDays = null;

        for (DeliverableBonds deliverable : bfmd.getDeliverables()) {
            String isin = deliverable.getIsin();

            SecurityMasterData smd = smdDAO.findByIsin(isin).orElse(null);
            if (smd == null || smd.getCashFlows() == null || smd.getCashFlows().isEmpty()) {
                skipped.add(isin + " (anagrafica o flussi mancanti)");
                continue;
            }
            InstrumentQuote quote = instrumentQuoteDAO.findByMasterDataCode(isin);
            if (quote == null) {
                skipped.add(isin + " (prezzo mancante)");
                continue;
            }

            // tutti i titoli del paniere devono avere la stessa convenzione di regolamento
            if (settlementDays == null) {
                settlementDays = smd.getBusinessDays();
            } else if (!settlementDays.equals(smd.getBusinessDays())) {
                throw new IllegalStateException("Giorni di regolamento diversi nel paniere di "
                        + futureIsin + ": " + settlementDays + " e " + smd.getBusinessDays() + " (" + isin + ")");
            }

            CashFlowSchedule schedule = new CashFlowSchedule(Utils.convertCashFlow(smd.getCashFlows()),
                    smd.getAccrualDaycount(), smd.getFrequency(), smd.getCompounding());

            deliverables.add(new DeliverableBond(isin, schedule, spotCleanPrice(quote), deliverable.getBondCf()));
        }

        if (!skipped.isEmpty()) {
            log.warn("Future {}: titoli del paniere esclusi: {}", futureIsin, skipped);
        }
        if (deliverables.isEmpty()) {
            throw new IllegalStateException("Nessun titolo del paniere utilizzabile per " + futureIsin);
        }

        // regolamento a pronti: oggi + giorni di regolamento dei titoli (T+2 per i BTP)
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, settlementDays);
        // il MBTP liquida sul prezzo del CTD rilevato all'expiry: la sua data valuta e' expiry + T+2
        LocalDate deliveryDate = calendar.getNextBusinessDate(expiryDate, settlementDays);

        return new Basket(bfmd.getCurrency(), valuationDate, deliveryDate, deliverables);
    }

    private static XRBForwardInput toInput(String futureIsin, Basket b, DiscountCurve repoCurve,
            Double quotedFuturePrice) {
        return new XRBForwardInput(futureIsin, b.valuationDate(), b.deliveryDate(),
                repoCurve, REPO_DAYCOUNT, b.deliverables(), quotedFuturePrice);
    }

    /**
     * Curva a un nodo che riproduce esattamente il tasso repo semplice alla
     * deliveryDate: DF = 1 alla valuationDate, DF = 1 / (1 + r * t) alla
     * deliveryDate. Tra le due date YieldCurve interpola log-linearmente
     * (cedole intermedie capitalizzate al tasso continuo equivalente:
     * differenza trascurabile rispetto all'interesse semplice).
     */
    private static DiscountCurve singleRateCurve(double repoRate, Basket b) {
        double tau = YieldCurve.yearFraction(b.valuationDate(), b.deliveryDate(), REPO_DAYCOUNT);
        double df = YieldCurve.discountFactorFromRate(repoRate, tau, Compounding.SIMPLE);
        int days = (int) ChronoUnit.DAYS.between(b.valuationDate(), b.deliveryDate());
        // ADATTARE se YieldCurve richiede un tipo Currency diverso dall'entity di anagrafica
        return YieldCurve.fromDiscountFactors(b.valuationDate(), java.util.Currency.getInstance(b.currency().getIsoCode()),
                List.of(new CurveNode(days, df, null)));
    }

    private DiscountCurve loadCurve(String curveCode, LocalDate valuationDate) {
        if (curveCode == null || curveCode.isBlank()) {
            throw new IllegalArgumentException("Codice curva repo mancante");
        }
        DiscountCurve curve = marketDataService.getDiscountCurve(curveCode);
        if (curve == null) {
            throw new NoSuchElementException("Curva non trovata: " + curveCode);
        }
        if (curve.getValuationDate().isAfter(valuationDate)) {
            throw new IllegalArgumentException("La curva " + curveCode + " ha data "
                    + curve.getValuationDate() + ", successiva alla valuationDate " + valuationDate);
        }
        return curve;
    }

    /**
     * Prezzo secco a pronti del titolo. Per una strategia cash-and-carry
     * (compro il titolo, vendo il future) il prezzo realistico e' l'ASK; per un
     * prezzo teorico "equo" e' il MID. Si mantiene il BID per coerenza con
     * l'implementazione precedente: da rivedere.
     */
    private static double spotCleanPrice(InstrumentQuote quote) {
        return quote.getBid();
    }
}
