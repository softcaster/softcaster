/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import java.time.LocalDate;
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
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.dto.XRBForwardResult;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.softcaster.engine.enums.CurveRole;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.provider.enums.RequestType;
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
        testCashFlowInstrumentCalculator();
        // testFxForwardCalculator();
        testBondForwardCalculator();
        // testBlack76Pricer();
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
}
