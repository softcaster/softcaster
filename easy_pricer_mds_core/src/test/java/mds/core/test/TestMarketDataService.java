/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.core.data.YieldCurve;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.TokenItem;
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
    YieldCurveDAO yieldCurveDAO;

    private void testYieldCurve() {
        // 1) Creazione curva
        marketDataService.addYieldCurve("CmeGroupProvider", "TERMSOFR");
        
        // 2) Update
        marketDataService.updateYieldCurve("CmeGroupProvider", "TERMSOFR");
        
        // 3) Richiesta tasso
        LocalDate today = LocalDate.now();
        LocalDate settlement = today.plusDays(42);
        double rate = marketDataService.getYieldCurveRate("TERMSOFR", settlement);
        System.out.println(rate);
    }

    private void testUpdateBondPrice() {
        Map<String, List<String>> tokenList = new HashMap<>();
        tokenList.computeIfAbsent("EuroNextProvider", k -> new ArrayList<>()).add("IT0001086567");
        List<TokenItem> tokens = new ArrayList<>();
        tokens.add(new TokenItem("EuroNextProvider","IT0001086567"));
        marketDataService.updateSpotPrice(tokens,Market.BONDS);
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

    public static void main(String[] args) {
        FileUtil.initializeLogger();
        FileUtil.initializePython();
        
        // 1. Avvio il contesto di Spring Boot caricando l'application.properties di test
        ApplicationContext context = SpringApplication.run(TestMarketDataService.class, args);

        // 2. Recupero l'istanza della classe di test gestita da Spring (con l'autowired funzionante)
        TestMarketDataService testRunner = context.getBean(TestMarketDataService.class);

        // 3. Eseguo il test
        //testRunner.testUpdateBondPrice();
        //testRunner.testSpotPrice();
        //testRunner.testDbAccess();
        testRunner.testYieldCurve();
    }
}
