/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package investing_test;

import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;
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

        bid = provider.getCurrencyQuote("EURUSD").getData().bid();
        ask = provider.getCurrencyQuote("EURUSD").getData().ask();
        System.out.println(bid + "\t" + ask);

    }

    private static void testItaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getItYieldCurve();
        for(Node n: nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    private static void testUsaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getUsYieldCurve();
        for(Node n: nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        System.out.println("########## IT Yield Curve ##########");
        testItaYieldCurves();
        System.out.println("");
        System.out.println("########## US Yield Curve ##########");
        testUsaYieldCurves();
    }
}
