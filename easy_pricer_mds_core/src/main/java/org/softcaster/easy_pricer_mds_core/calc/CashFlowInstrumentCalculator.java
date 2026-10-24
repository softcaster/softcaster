package org.softcaster.easy_pricer_mds_core.calc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.easy_pricer_mds_core.helpers.FltSecurityMasterDataHelper;
import org.softcaster.engine.analytics.FRBPricer;
import org.softcaster.engine.analytics.XRBPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.CashFlowAnalytics;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.FRBInput;
import org.softcaster.engine.dto.FRBResult;
import org.softcaster.engine.dto.FloatingCouponSpec;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.springframework.stereotype.Service;

// package e import come nel file originale
// + import java.util.List;
// + import java.util.NoSuchElementException;
// + import java.util.Objects;
/**
 * Costruisce gli input dei pricer a partire dall'anagrafica titoli e invoca i
 * pricer dell'engine per gli strumenti con cash flow.
 *
 * Errori: - NoSuchElementException -> titolo non trovato (REST: 404) -
 * IllegalArgumentException -> dati di input non validi o tipo di titolo errato
 * (REST: 400)
 */
@Service("cashFlowInstrumentCalculator")
public class CashFlowInstrumentCalculator {

    /**
     * Valore di 1 basis point nell'unita' in cui sono espressi i yield. 0.01 se
     * il yield e' in percentuale (4.25), 0.0001 se e' in decimale (0.0425).
     * VERIFICARE contro l'unita' restituita da XRBResult.analytics().yield().
     */
    private static final double ONE_BP = 0.01;

    private final SecurityMasterDataDAO smdDAO;
    private final XRBPricer xrbPricer;
    private final FRBPricer frbPricer;
    private final FltSecurityMasterDataHelper fltHelper;

    public CashFlowInstrumentCalculator(SecurityMasterDataDAO smdDAO,
            XRBPricer xrbPricer,
            FRBPricer frbPricer,
            FltSecurityMasterDataHelper fltHelper) {
        this.smdDAO = Objects.requireNonNull(smdDAO);
        this.xrbPricer = Objects.requireNonNull(xrbPricer);
        this.frbPricer = Objects.requireNonNull(frbPricer);
        this.fltHelper = Objects.requireNonNull(fltHelper);
    }

    // ------------------------------------------------------------------------
    // -- Fixed Rate Bond
    // ------------------------------------------------------------------------
    public XRBInput getXRBInput(String code, LocalDate officialDate, double cleanPrice) {
        return getXRBInput(officialDate, cleanPrice, loadSecurity(code));
    }

    public XRBInput getXRBInput(LocalDate officialDate, double cleanPrice, SecurityMasterData smd) {
        CashFlowSchedule schedule = fixedSchedule(smd);
        return new XRBInput(smd.getCode(), valuationDate(smd, officialDate), cleanPrice, schedule);
    }

    public XRBResult fixedIncomeValuation(XRBInput input) {
        return xrbPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    // ------------------------------------------------------------------------
    // -- Floating Rate Bond
    // ------------------------------------------------------------------------
    public FRBInput getFRBInput(String code, LocalDate officialDate, double cleanPrice, FloatingCouponSpec spec) {
        SecurityMasterData smd = loadSecurity(code);
        if (!(smd instanceof FltSecurityMasterData flt)) {
            throw new IllegalArgumentException("Il titolo " + code + " non e' un floating rate bond");
        }
        return getFRBInput(officialDate, cleanPrice, spec, flt);
    }

    public FRBInput getFRBInput(LocalDate officialDate, double cleanPrice, FloatingCouponSpec spec,
            FltSecurityMasterData flt) {
        Objects.requireNonNull(flt, "securityMasterData");
        requireCashFlows(flt);
        // le cedole future vengono proiettate qui (mds_core), l'engine riceve flussi completi
        List<CashFlow> flows = fltHelper.completeFltSecurityCashFlow(flt);
        CashFlowSchedule schedule = new CashFlowSchedule(flows, flt.getAccrualDaycount(),
                flt.getFrequency(), flt.getCompounding());
        return new FRBInput(flt.getCode(), valuationDate(flt, officialDate), cleanPrice, schedule, spec);
    }

    public FRBResult fltIncomeValuation(FRBInput input) {
        return frbPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    // ------------------------------------------------------------------------
    // -- Calcoli di supporto
    // ------------------------------------------------------------------------
    public double getAccruals(SecurityMasterData smd, LocalDate accrualDate) {
        CashFlowSchedule s = fixedSchedule(smd);
        return xrbPricer.calculateAccruedInterest(s.flows(), accrualDate, s.daycount(), s.frequency());
    }

    public double getZSpread(SecurityMasterData smd, double dirtyPrice,
            LocalDate valuationDate, DiscountCurve curve) {
        Objects.requireNonNull(curve, "curve");
        CashFlowSchedule s = fixedSchedule(smd);
        return xrbPricer.calculateZSpread(s.flows(), dirtyPrice, valuationDate, curve);
    }

    /**
     * Riprezza il titolo con il yield spostato di {@code basisPoints}.
     * {@code ytm} deve essere nella stessa unita' usata da ONE_BP.
     *
     * @param smd
     * @param officialDate
     * @param ytm
     * @param basisPoints
     * @return
     */
    public double repriceBondForYieldShift(SecurityMasterData smd, LocalDate officialDate,
            double ytm, double basisPoints) {
        CashFlowSchedule s = fixedSchedule(smd);
        double shiftedYield = ytm + basisPoints * ONE_BP;
        return xrbPricer.calculatePrice(s.flows(), shiftedYield, valuationDate(smd, officialDate),
                s.daycount(), s.compounding(), s.frequency());
    }

    public double calculatePrice(SecurityMasterData smd, LocalDate officialDate, DiscountCurve curve) {
        Objects.requireNonNull(curve, "curve");
        CashFlowSchedule s = fixedSchedule(smd);
        return xrbPricer.calculatePrice(s.flows(), curve, valuationDate(smd, officialDate),
                s.daycount(), s.frequency());
    }

    // ------------------------------------------------------------------------
    // -- Entry point REST
    // ------------------------------------------------------------------------
    public PricingResponse bondValuation(PricingRequest request) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(request.referenceDate, "referenceDate");
        Objects.requireNonNull(request.referencePrice, "referencePrice");
        XRBInput input = getXRBInput(request.isin, request.referenceDate, request.referencePrice);
        XRBResult result = fixedIncomeValuation(input);
        CashFlowAnalytics a = result.analytics();

        PricingResponse response = new PricingResponse();
        response.accruedInterest = a.accruedInterest();
        response.convexity = a.convexity();
        response.dv01 = a.dv01();
        response.modifiedDuration = a.modifiedDuration();
        response.yieldToMaturity = a.yield();
        // Restano null (non calcolati per un fixed rate bond):
        // macaulayDuration, discountMargin, marginDuration, presentValue,
        // shortBondYield, yieldToMaturityPV.
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------
    private SecurityMasterData loadSecurity(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Codice titolo mancante");
        }
        SecurityMasterData smd = smdDAO.findByCodeWithCashFlowAndHolidays(code);
        if (smd == null) {
            throw new NoSuchElementException("Titolo non trovato: " + code);
        }
        return smd;
    }

    private static void requireCashFlows(SecurityMasterData smd) {
        if (smd.getCashFlows() == null || smd.getCashFlows().isEmpty()) {
            throw new IllegalArgumentException("Il titolo " + smd.getCode() + " non ha cash flow");
        }
    }

    /**
     * Unico punto in cui si costruisce lo schedule di un titolo a tasso fisso.
     */
    private static CashFlowSchedule fixedSchedule(SecurityMasterData smd) {
        Objects.requireNonNull(smd, "securityMasterData");
        requireCashFlows(smd);
        List<CashFlow> flows = Utils.convertCashFlow(smd.getCashFlows());
        return new CashFlowSchedule(flows, smd.getAccrualDaycount(),
                smd.getFrequency(), smd.getCompounding());
    }

    /**
     * Unico punto in cui si calcola la valuation date.
     *
     * @param smd
     * @param officialDate
     * @return
     */
    public static LocalDate valuationDate(MasterData smd, LocalDate officialDate) {
        Objects.requireNonNull(officialDate, "officialDate");
        Calendar calendar = new Calendar(smd.getCurrencyList());
        return calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());
    }
}
