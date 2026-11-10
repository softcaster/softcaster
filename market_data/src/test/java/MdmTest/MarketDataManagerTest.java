/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MdmTest;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.MarketDataManagerFactory;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.jsoup.nodes.Element;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
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
        //testEurext();
        //testBorsaItaliana();
        //testTwelvedata();
        //testCmeGroup();
        testMutuiIt();
    }

    private void testEurext() {
        RequestParamManager params = new RequestParamManager();
        params.add("FBTP");
        MarketDataManager mdm = factory.getInstance("Eurex", params);
        if (mdm != null) {
            Node node = mdm.getQuote("DE000F2HLQE9", Market.FUTURES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testBorsaItaliana() {
        MarketDataManager mdm = factory.getInstance("BorsaItaliana", null);
        if (mdm != null) {
            Node node = mdm.getQuote("IT0001086567", Market.BONDS);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testTwelvedata() {
        MarketDataManager mdm = factory.getInstance("Twelvedata", null);
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
        MarketDataManager mdm = factory.getInstance("CmeGroup", null);
        if (mdm != null) {
            /*
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
             */
            String tickerFx = "2674@M6EZ6";
            String tickerBd = "307@ZBZ6";
            Node node = mdm.getQuote(tickerBd, Market.FUTURES);
            if (node != null) {
                System.out.println(node.symbol() + "\t" + node.data().bid());
            }
        }
    }

    private void testMutuiIt() throws IOException {

        MarketDataManager mdm = factory.getInstance("MutuiIt", null);
        if (mdm != null) {
            List<Node> rates = mdm.getRates("");
        }
    }
}
