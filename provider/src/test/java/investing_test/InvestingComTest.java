/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package investing_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.frankfurter.ExchRate;
import org.softcaster.provider.frankfurter.FrankfurterApiClient;
import org.softcaster.provider.investing.InvestingComProvider;

/**
 *
 * @author ep
 */
public class InvestingComTest {

    private static void testCurrencyPairs() {
        InvestingComProvider provider = InvestingComProvider.getInstance();
        double bid = provider.getCurrencyQuote("EURUSD").getData().bid();
        double ask = provider.getCurrencyQuote("EURUSD").getData().ask();
        System.out.println(bid + "\t" + ask);
    }

    private static void testItaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getItYieldCurve();
        for (Node n : nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    private static void testUsaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getUsYieldCurve();
        for (Node n : nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        //testCurrencyPairs();
        //System.out.println("########## IT Yield Curve ##########");
        // testItaYieldCurves();
        /*
        System.out.println("");
        System.out.println("########## US Yield Curve ##########");
        testUsaYieldCurves();
         */
        FrankfurterApiClient client = new FrankfurterApiClient();

        List<ExchRate> rates;
        try {
            rates = client.getExchangeRates();
            for (ExchRate rate : rates) {
                System.out.println(rate.base + rate.quote + ":" + rate.rate);
            }
        } catch (JsonProcessingException ex) {
            Logger.getLogger(InvestingComTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
