/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.engine;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import static org.softcaster.engine.Test.BOND;
import static org.softcaster.engine.Test.CRR;
import static org.softcaster.engine.Test.CURR;
import static org.softcaster.engine.Test.DATE;
import static org.softcaster.engine.Test.FXFWD;
import static org.softcaster.engine.Test.GAK;
import static org.softcaster.engine.Test.YCURVE;
import org.softcaster.engine.analytics.BlackAndScholesPricer;
import org.softcaster.engine.analytics.BondForwardPricer;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.analytics.CRRBinomialPricer;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.cashflow.BackwardScheduleGenerator;
import org.softcaster.engine.cashflow.BulletAmortizationStrategy;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.ForwardScheduleGenerator;
import org.softcaster.engine.cashflow.FrenchAmortizationStrategy;
import org.softcaster.engine.cashflow.HolidayCalendar;
import org.softcaster.engine.cashflow.PaymentPeriod;
import org.softcaster.engine.config.EngineAutoConfiguration;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.BondOptionInputData;
import org.softcaster.engine.dto.ForwardBaseInputData;
import org.softcaster.engine.dto.FxOptionInputData;
import org.softcaster.engine.dto.OptionData;
import org.softcaster.engine.dto.OptionOutputData;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.engine.enums.OptionStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.utils.CashFlowExporter;
import org.softcaster.engine.utils.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

/**
 *
 * @author softc
 */
class DummyCaLendar implements HolidayCalendar {

    @Override
    public boolean isHoliday(LocalDate date) {
        return false;
    }
}

enum Test {
    BOND, BAS, GAK, LOAN, DATE, CRR, FXFWD, BNDFWD, CURR, YCURVE
}

@SpringBootApplication
@Import(EngineAutoConfiguration.class)
public class Engine {

    @Autowired
    @Qualifier("bondPricer") // Indica a Spring esattamente QUALE bean usare
    private BondPricer bondPricer;

    @Autowired
    @Qualifier("bondFwdPricer")
    private BondForwardPricer bondForwardPricer;

    @Autowired
    @Qualifier("basPricer")
    private BlackAndScholesPricer blackAndScholesPricer;

    @Autowired
    @Qualifier("gakPricer")
    private GarmanKohlhagenPricer garmanKohlhagenPricer;

    @Autowired
    @Qualifier("crrPricer")
    private CRRBinomialPricer cRRBinomialPricer;

    @Autowired
    @Qualifier("fxFwdPricer")
    private FxForwardPricer fxForwardPricer;

    private void testGarmanKohlhagenPricer() {
        FxOptionInputData input = new FxOptionInputData();

        LocalDate settlement = LocalDate.of(2026, 5, 11);
        input.setValuationDate(settlement);

        LocalDate maturity = LocalDate.of(2027, 5, 11);
        input.setMaturityDate(maturity);

        input.setDomesticRate(0.05);
        input.setForeignRate(0.03);
        input.setUnderlyingReferencePrice(1.1);
        input.setDaycount(DaycountBasis.ACT_365);
        OptionData od = new OptionData(1.1, 0.1, OptionStyle.EUROPEAN, OptionType.CALL);
        input.setOptionData(od);
        OptionOutputData output = garmanKohlhagenPricer.priceCall(input);
        System.out.println(output.getPrice());
        System.out.println(output.getDelta());
        double impliedVol = garmanKohlhagenPricer.calculateImpliedVolatility(input, output.getPrice());
        System.out.println(impliedVol);
    }

    private void testCRRBinomialPricer() {
        FxOptionInputData input = new FxOptionInputData();

        LocalDate settlement = LocalDate.of(2026, 5, 11);
        input.setValuationDate(settlement);

        LocalDate maturity = LocalDate.of(2027, 5, 11);
        input.setMaturityDate(maturity);

        input.setDomesticRate(0.05);
        input.setForeignRate(0.03);
        input.setUnderlyingReferencePrice(1.1);
        input.setDaycount(DaycountBasis.ACT_365);
        OptionData od = new OptionData(1.1, 0.1, OptionStyle.EUROPEAN, OptionType.CALL);
        input.setOptionData(od);
        OptionOutputData output = cRRBinomialPricer.priceCall(input);
        System.out.println(output.getPrice());
    }

    private void testBlackAndScholesPricer() {
        BondOptionInputData input = new BondOptionInputData();

        LocalDate settlement = LocalDate.of(2026, 5, 11);
        input.setValuationDate(settlement);
        LocalDate maturity = LocalDate.of(2027, 5, 11);
        input.setMaturityDate(maturity);
        input.setUnderlyingReferencePrice(101.);
        input.setDaycount(DaycountBasis.ACT_365);
        OptionData od = new OptionData(100., 0.2, OptionStyle.EUROPEAN, OptionType.CALL);
        input.setOptionData(od);

        OptionOutputData output = blackAndScholesPricer.priceCall(input);
        System.out.println(output.getPrice());
        System.out.println(output.getDelta());
        double impliedVol = blackAndScholesPricer.calculateImpliedVolatility(input, output.getPrice(), OptionType.CALL);
        System.out.println(impliedVol);
    }

    private void testDateParser() {
        String strDate = "20260511";
        LocalDate dt = DateParser.parse(strDate);
        System.out.println(dt);
    }

    private void testBondPricer() {
        BackwardScheduleGenerator bsg = new BackwardScheduleGenerator();
        LocalDate effectiveDate = LocalDate.of(2002, 1, 1);
        LocalDate terminationDate = LocalDate.of(2033, 1, 2);
        Frequency freq = Frequency.fromId(2);

        BusinessDayConvention bdc = BusinessDayConvention.fromId(3);
        DaycountBasis daycount = DaycountBasis.fromId(5);

        DummyCaLendar dummy = new DummyCaLendar();
        List<PaymentPeriod> periods = bsg.generate(effectiveDate, terminationDate, freq, bdc, daycount, dummy);
        BulletAmortizationStrategy bas = new BulletAmortizationStrategy();
        List<CashFlow> flows = bas.generateCashFlows(100., 0.06, periods, DaycountBasis.ACT_ACT_ICMA);

        LocalDate valuationDate = LocalDate.of(2026, 5, 13);

        double irr = bondPricer.calculateYtm(flows, 114.08, valuationDate, DaycountBasis.ACT_365, Compounding.COMPOUNDED, freq);
        System.out.println(irr);
        double accruedInterest = bondPricer.calculateAccruedInterest(flows, valuationDate, DaycountBasis.ACT_365, freq);
        System.out.println(accruedInterest);
        double modifiedDuration = bondPricer.calculateModifiedDuration(flows, irr, valuationDate, daycount, freq);
        System.out.println(modifiedDuration);
        double convexity = bondPricer.calculateConvexity(flows, irr, 113.34, valuationDate, daycount, Compounding.COMPOUNDED);
        System.out.println(convexity);
    }

    private void testFrenchAmortizationStrategy() {
        LocalDate effectiveDate = LocalDate.of(2026, 5, 8);
        LocalDate terminationDate = LocalDate.of(2027, 5, 8);
        Frequency freq = Frequency.BI_MONTHLY;
        BusinessDayConvention bdc = BusinessDayConvention.FORWARD;
        DaycountBasis daycount = DaycountBasis.ACT_365;
        DummyCaLendar dummy = new DummyCaLendar();

        ForwardScheduleGenerator fsg = new ForwardScheduleGenerator();
        List<PaymentPeriod> periods = fsg.generate(effectiveDate, terminationDate, freq, bdc, daycount, dummy);

        FrenchAmortizationStrategy fas = new FrenchAmortizationStrategy();
        List<CashFlow> flows = fas.generateCashFlows(10000., 0.03, periods, daycount);

        for (CashFlow cf : flows) {
            System.out.println(cf.accrualStart() + "," + cf.accrualEnd() + "," + cf.interest() + "," + cf.principal());
        }
        try {
            CashFlowExporter.toCsv(flows, "piano_ammortamento.csv", ";");
            System.out.println("File CSV generato con successo!");
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }

    private void testFxForwardPricer() {

        ForwardBaseInputData input = new ForwardBaseInputData();

        LocalDate settlement = LocalDate.of(2026, 5, 11);
        input.setValuationDate(settlement);

        LocalDate maturity = LocalDate.of(2027, 5, 11);
        input.setMaturityDate(maturity);

        input.setDomesticRate(0.05);
        input.setForeignRate(0.03);
        input.setUnderlyingReferencePrice(1.1);
        input.setDaycount(DaycountBasis.ACT_365);
        input.setCompounding(Compounding.SIMPLE);

        double F = fxForwardPricer.forwardPrice(input);
        System.out.println(F);
        System.out.println((F - input.getUnderlyingReferencePrice()) * 10000);
    }

    private void testBondForwardPricer() {

    }

    private void testCurrency() {
        Currency c1 = Currency.getInstance("USD");
        System.out.println(c1.getCurrencyCode());
        System.out.println(c1.getDisplayName());
        System.out.println(c1.getSymbol());
        System.out.println(c1.getDefaultFractionDigits());

        System.out.println();

        c1 = Currency.getInstance("JPY");
        System.out.println(c1.getCurrencyCode());
        System.out.println(c1.getDisplayName());
        System.out.println(c1.getSymbol());
        System.out.println(c1.getDefaultFractionDigits());
    }

    private void testYieldCurve() {
        LocalDate officialDate = LocalDate.of(2026, 5, 14);
        Currency currency = Currency.getInstance("EUR");
        List<CurveNodeInput> inputs = new ArrayList<>();
        CurveNodeInput node;
        // 1 Giorno
        node = new CurveNodeInput("", new Offset(1, OffsetType.DAYS), 0.01928, DaycountBasis.ACT_360, Compounding.SIMPLE);
        inputs.add(node);
        // 1 Mese
        node = new CurveNodeInput("", new Offset(1, OffsetType.MONTHS), 0.02, DaycountBasis.ACT_360, Compounding.SIMPLE);
        inputs.add(node);
        // 3 Mesi
        node = new CurveNodeInput("", new Offset(3, OffsetType.MONTHS), 0.03, DaycountBasis.ACT_360, Compounding.SIMPLE);
        inputs.add(node);
        // 6 Mesi
        node = new CurveNodeInput("", new Offset(6, OffsetType.MONTHS), 0.035, DaycountBasis.ACT_360, Compounding.SIMPLE);
        inputs.add(node);
        // 1 Anno
        node = new CurveNodeInput("", new Offset(1, OffsetType.YEARS), 0.04, DaycountBasis.ACT_365, Compounding.COMPOUNDED);
        inputs.add(node);
        // 2 Anni
        node = new CurveNodeInput("", new Offset(2, OffsetType.YEARS), 0.045, DaycountBasis.ACT_365, Compounding.COMPOUNDED);
        inputs.add(node);

        YieldCurve curve = new YieldCurve(officialDate, currency, inputs);

        LocalDate targetDate = LocalDate.of(2026, 9, 27);
        double df = curve.getDiscountFactor(targetDate);
        System.out.println(df);

        targetDate = LocalDate.of(2026, 5, 21);
        df = curve.getDiscountFactor(targetDate);
        System.out.println(df);

    }

    private void runTest(Test test) {
        switch (test) {
            case BOND ->
                testBondPricer();
            case BAS ->
                testBlackAndScholesPricer();
            case GAK ->
                testGarmanKohlhagenPricer();
            case LOAN ->
                testFrenchAmortizationStrategy();
            case DATE ->
                testDateParser();
            case CRR ->
                testCRRBinomialPricer();
            case FXFWD ->
                testFxForwardPricer();
            case CURR ->
                testCurrency();
            case YCURVE ->
                testYieldCurve();
        }
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        // 1. Registra la configurazione dove hai definito @Bean(name="btpCalculator")
        context.register(EngineAutoConfiguration.class);

        // 2. Registra la classe Engine stessa per permettere l'autowire dei suoi campi
        context.register(Engine.class);

        // 3. Refresh del contesto per attivare i bean
        context.refresh();

        // 4. Recupera Engine e lancia il test
        Engine engine = context.getBean(Engine.class);

        engine.runTest(YCURVE);
    }
}
