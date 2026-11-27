/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MdmTest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.MarketDataManagerFactory;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;
import org.softcaster.market_data.services.Eurext.DeliverableBond;
import org.softcaster.market_data.services.Eurext.EurexApiClient;
import org.softcaster.market_data.services.Eurext.FutureContract;
import org.softcaster.market_data.services.Eurext.OptionSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    //"org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
//@EntityScan("org.softcaster.core.data")
//@EnableJpaRepositories("org.softcaster.core.data")
public class MarketDataManagerTest implements CommandLineRunner {

    @Autowired
    MarketDataManagerFactory factory;

    public static void main(String[] args) {
        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        // Avvia l'applicazione tramite Spring Boot 
        SpringApplication.run(MarketDataManagerTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        testEurext();
        // testBorsaItaliana();
        // testTwelvedata();
        // testCmeGroup();
        // testMutuiIt();
        // testEcb();
        // testBlueGammaOis();
        // providers();
        // testCnbc();
        // testCsv();
        // testBondBlox();
        // testCBonds();
    }

    private void testEurext() {
        MarketDataManager mdm = factory.getInstance("Eurex");
        if (mdm != null) {
            Node node = mdm.getQuote("FESX@DE0009652388", Market.FUTURES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }

            // call 5800 dicembre 2026: ogni serie di opzione ha il suo ISIN
            node = mdm.getQuote("OESX@DE000F0N5VS5", Market.OPTIONS);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }

            //EurexApiClient.findFutures("FESX");
            // ISIN da scadenza, strike e call/put: call 6200 dicembre 2026
            try {
                String isin = EurexApiClient.findIsin("OESX", LocalDate.of(2027, 3, 19), 6000, "C");
                if (isin != null) {
                    node = mdm.getQuote("OESX@" + isin, Market.OPTIONS);
                    if (node != null) {
                        System.out.println(node.symbol() + "\t" + node.data().bid());
                    }
                }
            } catch (Exception ex) {
                System.out.println("findIsin failed: " + ex.getMessage());
            }

            // catena di dicembre 2026, strike 6000-6400 (call e put)
            try {
                List<OptionSeries> chain = EurexApiClient.findSeries("OESX", LocalDate.of(2026, 12, 18), 6000.0, 6400.0, null);
                for (OptionSeries s : chain) {
                    System.out.println(s.isin() + "\t" + s.strike() + "\t" + s.callPut() + "\t" + s.settlementPrice());
                }
            } catch (Exception ex) {
                System.out.println("findSeries failed: " + ex.getMessage());
            }

            // serie future btp
            try {
                List<FutureContract> chain = EurexApiClient.findFutures("FBTP");
                for (FutureContract fc : chain) {
                    System.out.println(fc.isin() + "\t" + fc.contractId() + "\t" + fc.contract() + "\t" + fc.expirationDate());
                }
            } catch (Exception ex) {
                System.out.println("findSeries failed: " + ex.getMessage());
            }
            // Deliverables future btp
            try {
                Map<Integer, List<DeliverableBond>> deliverables = EurexApiClient.findDeliverables("FBTP");
                deliverables.forEach((contractId, bonds) -> {
                    System.out.println("Contract ID: " + contractId);
                    for (DeliverableBond bond : bonds) {
                        System.out.println("  -> " + bond.bondIsin() + ";" + bond.coupon() + ";"
                                + bond.maturity() + ";" + bond.conversionFactor());
                    }
                });
            } catch (Exception ex) {
                System.out.println("findSeries failed: " + ex.getMessage());
            }
        }
    }

    private void testBorsaItaliana() {
        MarketDataManager mdm = factory.getInstance("BorsaItaliana");
        if (mdm != null) {
            Node node = mdm.getQuote("IT0001086567", Market.BONDS);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testTwelvedata() {
        MarketDataManager mdm = factory.getInstance("Twelvedata");
        if (mdm != null) {
            Node node = mdm.getQuote("EURUSD", Market.CURRENCIES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
            node = mdm.getQuote("EURCHF", Market.CURRENCIES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
            node = mdm.getQuote("EURCAD", Market.CURRENCIES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testCmeGroup() {
        MarketDataManager mdm = factory.getInstance("CmeGroup");
        if (mdm != null) {

            List<Node> nodes = mdm.getRates("TERMSOFR");
            if (nodes != null && !nodes.isEmpty()) {
                for (Node node : nodes) {
                    System.out.println(node.symbol() + "\t" + node.data().bid());
                }
            }

            System.out.println();

            nodes = mdm.getRates("TERMESTR");
            if (nodes != null && !nodes.isEmpty()) {
                for (Node node : nodes) {
                    System.out.println(node.symbol() + "\t" + node.data().bid());
                }
            }

            String tickerFx = "2674@M6EZ6";
            String tickerBd = "307@ZBZ6";
            Node node = mdm.getQuote(tickerBd, Market.FUTURES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testMutuiIt() throws IOException {

        MarketDataManager mdm = factory.getInstance("MutuiIt");
        if (mdm != null) {
            List<Node> rates = mdm.getRates("");
        }
    }

    private void testEcb() {

        MarketDataManager mdm = factory.getInstance("Ecb");
        if (mdm != null) {
            List<Node> rates = mdm.getRates("");
            for (Node node : rates) {
                System.out.println(node.offset().offsetType().getCode() + "\t" + node.offset().step() + "\t" + node.data().bid());
            }
        }
    }

    private void testBlueGammaOis() {
        // estr-swap-rates, euribor-swap-rates, chf-swap-rates, usd-swap-rates, cad-swap-rates
        MarketDataManager mdm = factory.getInstance("BlueGammaOis");
        if (mdm != null) {
            List<Node> rates = mdm.getRates("EURIBOR_OIS");
            if (rates != null && !rates.isEmpty()) {
                for (Node node : rates) {
                    System.out.println(node.offset().offsetType().getCode() + "\t" + node.offset().step() + "\t" + node.data().bid());
                }
            }
        }
    }

    private void testCnbc() {
        MarketDataManager mdm = factory.getInstance("Cnbc");
        if (mdm != null) {
            Node exchRate = mdm.getQuote("EURCHF", Market.CURRENCIES);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());

            exchRate = mdm.getQuote("EURCAD", Market.CURRENCIES);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());

            exchRate = mdm.getQuote("EURUSD", Market.CURRENCIES);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());

            exchRate = mdm.getQuote("IT1Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT2Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT3Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT4Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT5Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT6Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT7Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT8Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT9Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
            exchRate = mdm.getQuote("IT10Y", Market.BONDS);
            System.out.println(exchRate.symbol() + "\t" + exchRate.data().bid());
        }
    }

    private void testCsv() {
        MarketDataManager mdm = factory.getInstance("Csv");
        if (mdm != null) {
            List<Node> rates = mdm.getRates("ITAYIELD");
            if (rates != null && !rates.isEmpty()) {
                for (Node node : rates) {
                    System.out.println(node.symbol() + "\t" + node.data().bid());
                }
            }
        }
    }

    private void providers() {
        List<String> providers = factory.gerProviders();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                System.out.println(provider);
            }
        }
    }

    private void testBondBlox() {
        MarketDataManager mdm = factory.getInstance("BondBlox");
        if (mdm != null) {
            Node node = mdm.getQuote("US912810QL52", Market.BONDS);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid() + "\t" + node.data().ask());
            }
        }
    }

    private void testCBonds() {
        MarketDataManager mdm = factory.getInstance("CBonds");
        if (mdm != null) {
            // ultimo giorno lavorativo (getRates senza data), o una data precisa con getRates("CHF", data)
            List<Node> rates = mdm.getRates("CHF");
            if (rates != null && !rates.isEmpty()) {
                for (Node node : rates) {
                    System.out.println(node.symbol() + ";"
                            + node.data().bid() + ";"
                            + node.daycount().getCode() + ";"
                            + node.compounding().getCode() + ";"
                            + node.nodeType().getCode());
                }
            }
        }
    }
}
