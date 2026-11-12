/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.softcaster.core.data.AcDcBcyLink;
import org.softcaster.core.data.AcDcBcyLinkDAO;
import org.softcaster.core.data.AssetClass;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.SpreadCurveEntity;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.AccumulatorCalculator;
import org.softcaster.engine.cashflow.AccumulatorFixingDate;
import org.softcaster.engine.cashflow.AccumulatorScheduleGenerator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.analytics.AccumulatorPricer;
import org.softcaster.engine.dto.AccumulatorFixingInput;
import org.softcaster.engine.dto.AccumulatorInput;
import org.softcaster.engine.dto.AccumulatorResult;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.dto.XRBForwardResult;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.CurveRole;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine", // Il pacchetto della libreria engine
    "org.softcaster.provider" // Il pacchetto della libreria provider
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestPricer implements CommandLineRunner {

    @Autowired
    @Qualifier("marketDataService") // Indica a Spring esattamente QUALE bean usare
    private MarketDataService marketDataService;

    @Autowired
    CashFlowInstrumentCalculator cfCalculator;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    @Autowired
    BondForwardCalculator bndfwdCalculator;

    @Autowired
    AccumulatorCalculator acCalculator;
    
    @Autowired
    private FxFutureMasterDataDAO fxFutureMasterDataDAO;

    @Autowired
    YieldCurveEntityDAO yieldCurveEntityDAO;
    @Autowired
    SpreadCurveEntityDAO spreadCurveEntityDAO;
    @Autowired
    AcDcBcyLinkDAO acDcBcyLinkDAO;

    @Autowired
    private YieldCurveEntityDAO ycDAO;
    @Autowired
    private SpreadCurveEntityDAO scDAO;
    @Autowired
    CurrencyDAO currencyDAO;
    @Autowired
    AssetClassDAO assetClassDAO;

    public static void main(String[] args) {
        SpringApplication.run(TestPricer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Allinea curve e prezzi
        startUp();
        //testCashFlowInstrumentCalculator();
        // testFxForwardCalculator();
        //testBondForwardCalculator();
        // testBlack76Pricer();
        testAccumulatorCalculator();
    }

    private void startUp() {
        List<String> curveNames = yieldCurveEntityDAO.findNames();
        curveNames.addAll(spreadCurveEntityDAO.findNames());
        marketDataService.reloadCurves(curveNames);
        marketDataService.loadSpotPrice();
    }

    private void testCashFlowInstrumentCalculator() {
        double spotPrice = 105.26;
        XRBInput input = cfCalculator.getXRBInput("IT0003535157", marketDataService.getOfficialDate(), spotPrice);
        XRBResult result = cfCalculator.fixedIncomeValuation(input);
        System.out.println(result.analytics().yield());
        System.out.println(result.analytics().accruedInterest());
        System.out.println(result.analytics().modifiedDuration());
    }

    private void testFxForwardCalculator() {

        double spotPrice = marketDataService.getSpotPrice("EURUSD", RequestType.BID);
        FxForwardInput input = fxForwardCalculator.getFxForwardInput("M6EZ6", marketDataService.getOfficialDate(),
                spotPrice, "", "");
        FxForwardResult result = fxForwardCalculator.fxFwdValuation(input);
        if (result != null) {
            System.out.println(result.forwardRate());
            System.out.println(result.forwardPoints());
        }
    }

    private void testBondForwardCalculator() {
        double price = 111.;
        Currency currency = currencyDAO.findByIsoCode("EUR");
        AssetClass assetClass = assetClassDAO.findByCode("BFU");
        AcDcBcyLink link = acDcBcyLinkDAO.find(assetClass.getIdAssetClass(),
                currency.getIdCurrency(), CurveRole.DISCOUNT).orElse(null);
        if (link == null) {
            System.out.println("No Discount Curve... aborting");
            return;
        }
        String discountCurveName = "";
        if (link.isSpreadCurve()) {
            SpreadCurveEntity sc = scDAO.findByIdWithBase(link.getSpreadCurve());
            discountCurveName = sc.getCode();
        } else {
            YieldCurveEntity yc = ycDAO.findByIdYieldCurve(link.getCurveId());
            discountCurveName = yc.getCode();
        }
        XRBForwardInput input = bndfwdCalculator.getBondForwardInput("IT0026145778", marketDataService.getOfficialDate(), discountCurveName, price);
        XRBForwardResult result = bndfwdCalculator.bondFwdValuation(input);
        if (result != null) {
            System.out.println("fwd rate:    " + result.forwardRate());
            System.out.println("ctd isin:    " + result.ctd().isin());
            System.out.println("term repo:    " + result.termRepoRate());
            System.out.println("implied repo: " + result.ctd().impliedRepo());
            System.out.println("net basis:    " + result.ctd().netBasis());
            System.out.println("ctd yield:    " + result.ctd().yield());
            System.out.println("ctd mod dur:  " + result.ctd().modifiedDuration());
            System.out.println("ctd dv01:     " + result.ctd().dv01());
            System.out.println("future dv01:  " + result.futureDv01());
        }
    }

    private void testBlack76Pricer() {

        String fxFutCode = "M6EZ6";
        FxFutureMasterData fmd = fxFutureMasterDataDAO.findByCodeWithUnderlying(fxFutCode).orElse(null);
        if (fmd == null) {
            throw new NoSuchElementException("Future non trovato: " + fxFutCode);
        }

        LocalDate valuationDate = CashFlowInstrumentCalculator.valuationDate(fmd, marketDataService.getOfficialDate());
        double futurePrice = 1.12485;
        DiscountCurve usdCurve = marketDataService.getDiscountCurve("TERMSOFR");
        LocalDate optionExpiry = LocalDate.of(2026, 12, 4);
        double notional = 125000.;
        double volatility = 0.0641;
        ForwardOptionInput input = new ForwardOptionInput(
                "EUU Z6 C1.1000",
                valuationDate, // trade date
                futurePrice, // prezzo del future trimestrale sottostante (es. 6EZ6), non lo spot
                usdCurve, // il premio e' in USD: si sconta con la curva USD
                true, // premio pagato subito (vedi sotto)
                new OptionContract(org.softcaster.engine.enums.OptionType.PUT, ExerciseStyle.EUROPEAN, 1.135,
                        optionExpiry, // scadenza dell'OPZIONE, non la consegna del future
                        notional, // nozionale del 6E in EUR
                        List.of()),
                volatility);

        Black76Pricer pricer = new Black76Pricer();
        double premioSettlement = 0.0057;
        //volatility = ImpliedVolatility.solve(pricer, input, premioSettlement);
        //System.out.println(volatility);

        OptionResult result = pricer.calculate(input);
        System.out.println("Price: " + result.price() + "\t"
                + "Delta: " + result.greeks().delta() + "\t"
                + "Gamma: " + result.greeks().gamma() + "\t"
                + "Vega: " + result.greeks().vega() + "\t"
                + "Rho: " + result.greeks().rho()
        );
    }

    /**
     * Accumulator del sistema di riferimento (docs/accumulator/accum.xlsx)
     * prezzato costruendo a mano l'AccumulatorInput. In produzione l'input lo
     * costruisce AccumulatorCalculator.getAccumulatorInput a partire
     * dall'anagrafica (curve, spot e superficie di volatilita' dal DB).
     */
    private void testAccumulatorCalculator() {

        // ------------------------------------------------------------------
        // 1. Termini del contratto
        // ------------------------------------------------------------------
        LocalDate valuationDate = LocalDate.of(2026, 9, 23);   // conclusione = data di valutazione
        LocalDate firstFixing = LocalDate.of(2026, 9, 23);     // puo' coincidere con la conclusione
        LocalDate lastFixing = LocalDate.of(2026, 10, 22);
        Frequency frequency = Frequency.WEEKLY;                // periodi di regolamento dal primo fixing
        int spotLag = 2;                                       // regolamento = esercizio + 2 giorni lavorativi
        double strike = 1.17;
        double koBarrier = 1.2;                               // KO Trigger 1.17 UP
        BarrierObservation koObservation = BarrierObservation.CONTINUOUS; // Trigger Style American
        double leverage = 2.;                                  // standard; il pricer richiede leva >= 1
        double maxQuantity = 100_000.;                         // Bcy Max Amt: EUR senza leva
        int guaranteedPeriods = 2;                             // prime 3 settimane senza KO
        GuaranteeType guaranteeType = GuaranteeType.UNLEVERAGED; // Guaranteed = Unleveraged

        // ------------------------------------------------------------------
        // 2. Calendario: fixing sui giorni lavorativi EUR (valuta del fixing),
        //    regolamento in un giorno lavorativo EUR e USD
        // ------------------------------------------------------------------
        Calendar fixingCalendar = new Calendar(currencyDAO.findByIsoCode("EUR"));
        Calendar settlementCalendar = new Calendar(currencyDAO.findByIsoCode("EUR"), currencyDAO.findByIsoCode("USD"));
        List<AccumulatorFixingDate> fixingList = new AccumulatorScheduleGenerator().generate(firstFixing, lastFixing,
                frequency, spotLag, fixingCalendar, settlementCalendar);

        for (AccumulatorFixingDate fixing : fixingList) {
            System.out.println(fixing.fixingDate() + "\t" + fixing.exerciseDate() + "\t" + fixing.settlementDate());
        }

        // quantita' per fixing = massima / numero di fixing (100.000 / 22 = 4.545,45)
        double dailyQuantity = maxQuantity / fixingList.size();

        // fixing garantiti = quelli dei primi guaranteedPeriods periodi
        // (un periodo = fixing con la stessa data di regolamento)
        long guaranteedFixings = fixingList.stream()
                .map(AccumulatorFixingDate::settlementDate)
                .distinct()
                .limit(guaranteedPeriods)
                .mapToLong(settlement -> fixingList.stream().filter(f -> f.settlementDate().equals(settlement)).count())
                .sum();

        // ------------------------------------------------------------------
        // 3. Dati di mercato alla data di valutazione
        // ------------------------------------------------------------------
        double spot = 1.166;          // EUR/USD
        double volatility = 0.0685;   // annua, decimale (6,85%); qui piatta su tutti i fixing
        // tassi continui ACT/365 piatti al posto delle curve (TERMSOFR per USD, TERMESTR per EUR):
        // le curve in DB hanno la data ufficiale e non si possono usare per una data precedente
        double rUsd = 0.036;          // valuta domestica = di regolamento: sconta i flussi
        double rEur = 0.019;          // valuta estera = base: rendimento del sottostante

        // ------------------------------------------------------------------
        // 4. Un AccumulatorFixingInput per ogni fixing non ancora avvenuto
        // ------------------------------------------------------------------
        List<AccumulatorFixingInput> fixings = new ArrayList<>();
        for (int i = 0; i < fixingList.size(); i++) {
            AccumulatorFixingDate d = fixingList.get(i);
            // anni dalla valutazione al fixing e al regolamento (ACT/365); 0 per il fixing di oggi
            double t = ChronoUnit.DAYS.between(valuationDate, d.fixingDate()) / 365.;
            double ts = ChronoUnit.DAYS.between(valuationDate, d.settlementDate()) / 365.;
            fixings.add(new AccumulatorFixingInput(
                    i,                                          // posizione nel calendario completo (per la garanzia)
                    t,
                    spot * Math.exp((rUsd - rEur) * t),         // forward EUR/USD alla data di fixing
                    spot * Math.exp((rUsd - rEur) * ts),        // forward alla data di regolamento del periodo
                    Math.exp(-rUsd * ts),                       // sconto in USD dalla valutazione al regolamento
                    d.fixingDate().equals(d.exerciseDate()),    // ultimo fixing del periodo (KO osservato al regolamento)
                    volatility));                               // vol alla scadenza del fixing (da superficie: allo strike)
        }

        // ------------------------------------------------------------------
        // 5. Input e valutazione (valori per chi detiene il contratto, in USD)
        // ------------------------------------------------------------------
        AccumulatorInput input = new AccumulatorInput(
                "EURUSD_ACC",                   // identificativo, solo per i risultati
                valuationDate,
                AccumulatorType.ACCUMULATOR,    // il detentore compra EUR allo strike
                strike,
                koBarrier,
                koObservation,
                null,                           // barriera di knock-in: nessuna
                null,                           // effetto del knock-in: null se non c'e' la barriera
                leverage,
                dailyQuantity,
                (int) guaranteedFixings,
                guaranteeType,                  // NONE se e solo se 0 fixing garantiti
                false,                          // koAccrual: il fixing del KO non matura
                false,                          // knockedIn: KI gia' avvenuto (contratti in vita)
                spot,                           // le greche sono rispetto a questo
                fixings,
                List.of(),                      // quantita' maturate e non regolate: nessuna alla conclusione
                AccumulatorInput.DEFAULT_PATHS, // 50.000 sentieri
                AccumulatorInput.DEFAULT_SEED); // stesso seme, stesso risultato

        AccumulatorResult result = new AccumulatorPricer().calculate(input);
        double ccyMaxAmount = maxQuantity * strike;   // importo massimo in USD (115.000)
        System.out.printf("Fixing %d, quantita' per fixing %.2f, fixing garantiti %d%n",
                fixingList.size(), dailyQuantity, guaranteedFixings);
        System.out.printf("Valore %.2f USD (+/- %.2f) = %.6f%% di %.0f USD%n",
                result.value(), result.standardError(), 100 * result.value() / ccyMaxAmount, ccyMaxAmount);
        System.out.printf("P(KO) %.4f, quantita' attesa %.0f EUR, delta %.0f, gamma %.0f, vega %.2f USD per punto%n",
                result.knockOutProbability(), result.expectedQuantity(), result.delta(), result.gamma(), result.vega());
    }
}
