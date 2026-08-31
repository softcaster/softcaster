/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.YieldCurve;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.core.data.account.GlAccount;
import org.softcaster.core.data.account.GlAccountDAO;
import org.softcaster.core.data.account.GlAccountSlots;
import org.softcaster.core.data.account.GlAccountSlotsDAO;
import org.softcaster.easy_pricer_mds_core.DiscountFactorNode;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.TokenItem;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.calc.YieldCurveHelper;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.engine.curve.OrderedDiscountFactor;
import org.softcaster.engine.dto.ForwardBaseInputData;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.ecb.ECBProvider;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author softc
 */
@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine", // Il pacchetto della libreria engine
    "org.softcaster.provider" // Il pacchetto della libreria provider
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestMarketDataService {

    @Autowired
    @Qualifier("marketDataService") // Indica a Spring esattamente QUALE bean usare
    private MarketDataService marketDataService;

    @Autowired
    private BondCalculator bondCalculator;

    @Autowired
    YieldCurveDAO yieldCurveDAO;

    @Autowired
    private SecurityMasterDataDAO smdDAO;

    private void testYieldCurve() {

        marketDataService.loadCurveCurveRates("TERMSOFR");
        marketDataService.loadCurveCurveRates("TERMESTR");
        marketDataService.loadSpotPrice();

        org.softcaster.engine.curve.YieldCurve domesticYC = marketDataService.getYieldCurve("TERMSOFR");
        org.softcaster.engine.curve.YieldCurve foreignYC = marketDataService.getYieldCurve("TERMESTR");

        ForwardBaseInputData input = new ForwardBaseInputData();
        input.setForeignRateCurve(foreignYC);
        input.setDomesticRateCurve(domesticYC);
        input.setUnderlyingReferencePrice(marketDataService.getSpotPrice("EURUSD", RequestType.BID));
        input.setValuationDate(LocalDate.now());
        input.setMaturityDate(LocalDate.of(2026, 06, 05));

        FxForwardPricer pricer = new FxForwardPricer();
        double f = pricer.forwardPrice2(input);
        System.out.println(f);
    }

    private void testUpdateBondPrice() {
        Map<String, List<String>> tokenList = new HashMap<>();
        tokenList.computeIfAbsent("EuroNextProvider", k -> new ArrayList<>()).add("IT0001086567");
        List<TokenItem> tokens = new ArrayList<>();
        tokens.add(new TokenItem("EuroNextProvider", "IT0001086567"));
        marketDataService.updateSpotPrice(tokens, Market.BONDS);
    }

    private void testSpotPrice() {

        double spotPrice = marketDataService.getSpotPrice("IT0001086567", RequestType.BID);
        System.out.println(spotPrice);
    }

    private void testDbAccess() {
        List<YieldCurve> curves = yieldCurveDAO.findAll();
        for (YieldCurve yc : curves) {
            System.out.println(yc.getCode() + "\t" + yc.getCurrency().getIsoCode());
        }
    }

    private void testBondPricer() {
        SecurityMasterData smd = smdDAO.findByIsin("IT0004532559");
        if (smd != null) {
            // FMIRS ITAYIELD TERMESTR
            marketDataService.loadCurveCurveRates("ECBYC");
            org.softcaster.engine.curve.YieldCurve yieldCurve = marketDataService.getYieldCurve("ECBYC");
            if (yieldCurve != null) {
                double price = bondCalculator.calculatePrice(smd, marketDataService.getOfficialDate(), yieldCurve);
                System.out.println(price);
                System.out.println(marketDataService.getSpotPrice("IT0004532559", RequestType.ASK));
            }
        }
    }

    public static void main(String[] args) {
        FileUtil.initializeLogger();
        // FileUtil.initializePython();

        // 1. Avvio il contesto di Spring Boot caricando l'application.properties di test
        ApplicationContext context = SpringApplication.run(TestMarketDataService.class, args);

        // 2. Recupero l'istanza della classe di test gestita da Spring (con l'autowired funzionante)
        TestMarketDataService testRunner = context.getBean(TestMarketDataService.class);

        // 3. Eseguo il test
        //testRunner.testUpdateBondPrice();
        //testRunner.testSpotPrice();
        //testRunner.testDbAccess();
        //testRunner.testYieldCurve();
        //testRunner.testDiscountFactor();
        // testRunner.testEcbYieldCurve();
        testRunner.testBondPricer();
    }

    // Ricava i DF per una serie di date passate in input (ipotetiche scadenze
    // di coupons)
    private void testDiscountFactor() {
        List<LocalDate> maturities = new ArrayList<>();
        LocalDate today = marketDataService.getOfficialDate();
        LocalDate monthPlus = today.plusMonths(1);
        maturities.add(monthPlus);

        monthPlus = monthPlus.plusMonths(1);
        maturities.add(monthPlus);

        monthPlus = monthPlus.plusMonths(1);
        maturities.add(monthPlus);

        marketDataService.loadCurveCurveRates("TERMESTR");
        List<DiscountFactorNode> nodes = YieldCurveHelper.getDiscountFactors("TERMSOFR", marketDataService, maturities);
        if (nodes != null) {
            for (DiscountFactorNode node : nodes) {
                System.out.println(node.maturity() + " - " + node.bid());
            }
        }
    }

    private void testEcbYieldCurve() {

        ECBProvider provider = ECBProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("EcbYiedCurve");

        List<CurveNodeInput> rawNodes = YieldCurveHelper.getCNIList(nodes);
        LocalDate officialDate = marketDataService.getOfficialDate();
        org.softcaster.engine.curve.YieldCurve yc = new org.softcaster.engine.curve.YieldCurve(officialDate, Currency.getInstance("EUR"), rawNodes);

        List<OrderedDiscountFactor> dfs = yc.getOrderedDiscountFactors();
        for (OrderedDiscountFactor odf : dfs) {
            System.out.println(odf.date() + " : " + odf.discountFactor());
        }
    }
}
