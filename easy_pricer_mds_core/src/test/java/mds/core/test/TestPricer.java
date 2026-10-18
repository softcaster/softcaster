/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.data.CmdFutureMasterDataDAO;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FltSecurityMasterDataDAO;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;
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
        testCashFlowInstrumentCalculator();
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
}
