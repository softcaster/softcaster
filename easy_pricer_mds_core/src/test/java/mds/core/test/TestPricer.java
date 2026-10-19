/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import java.util.List;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FltSecurityMasterDataDAO;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;
import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.dto.XRBForwardResult;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
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
    YieldCurveEntityDAO yieldCurveEntityDAO;
    @Autowired
    SpreadCurveEntityDAO spreadCurveEntityDAO;

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    private FltSecurityMasterDataDAO fltSecurityMasterDataDAO;
    @Autowired
    private FxFutureMasterDataDAO fxFutureMasterDataDAO;

    @Autowired
    CurrencyDAO currencyDAO;

    public static void main(String[] args) {
        SpringApplication.run(TestPricer.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Allinea curve e prezzi
        startUp();
        //testCashFlowInstrumentCalculator();
        //testFxForwardCalculator();
        testBondForwardCalculator();
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
        XRBForwardInput input = bndfwdCalculator.getBondForwardInput("IT0026145778", marketDataService.getOfficialDate(), "TERMESTR", price);
        XRBForwardResult result = bndfwdCalculator.bondFwdValuation(input);
        if (result != null) {
            System.out.println("fwd rate:    " + result.forwardRate());
            System.out.println("ctd isin:    " + result.ctd().isin());
            System.out.println("term repo:    " + result.termRepoRate());
            System.out.println("implied repo: " + result.ctd().impliedRepo());
            System.out.println("net basis:    " + result.ctd().netBasis());
        }
    }
}
