/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package investing_test;

import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.cnbc.CnbcProvider;
import org.softcaster.provider.ecb.ECBProvider;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.eodhd.EodhdFxApiClient;
import org.softcaster.provider.frankfurter.FrankfurterProvider;
import org.softcaster.provider.investing.InvestingComProvider;
import org.softcaster.provider.twelvedata.TwelvedataProvider;

/**
 *
 * @author ep
 */
public class InvestingComTest {

    private static void testCurrencyPairs() {
        TwelvedataProvider provider = TwelvedataProvider.getInstance();
        Node node = provider.getMktQuote("EURUSD", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURCHF", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURJPY", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURCAD", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURAUD", Market.CURRENCIES);
        System.out.println(node.getData().bid());
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
        //testEcbClient();
        //testEodhdFxApiClient();
        //System.out.println("########## IT Yield Curve ##########");
        // testItaYieldCurves();
        /*
        System.out.println("");
        System.out.println("########## US Yield Curve ##########");
        testUsaYieldCurves();
         */
 
        CnbcProvider provider = CnbcProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("USYIELD");
        for(Node node:nodes) {
            System.out.println(node.getSymbol() + " " + node.getData().bid());
        }
         
        testEcbClient();
        //testCurrencyPairs();
    }

    private static void testEodhdFxApiClient() {
        EodhdFxApiClient client = new EodhdFxApiClient();
        System.out.println(client.getRealTimeExchangeRate("EURUSD.FOREX"));

        FrankfurterProvider provider = FrankfurterProvider.getInstance();
        System.out.println(provider.getMktQuote("EURUSD", Market.CURRENCIES).getData().bid());
    }

    private static void testEcbClient() {

        ECBProvider provider = ECBProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("EcbYiedCurve");

        for (Node node : nodes) {
            System.out.println(node.getSymbol() + ":" + node.getData().bid());
        }
    }
}
