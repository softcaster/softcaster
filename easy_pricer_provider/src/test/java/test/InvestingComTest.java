/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.io.IOException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import org.softcaster.marketdataprovider.MarketDataProviderHelper;
import org.softcaster.marketdataprovider.YieldNode;
import org.softcaster.marketdataprovider.euribor.EuriborRatesProvider;
import org.softcaster.marketdataprovider.investingcom.InvestingComProvider;

/**
 *
 * @author ep
 */
public class InvestingComTest {

    private static void testCurrencyPairs() {
        InvestingComProvider provider = InvestingComProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.investing.com";
        param.url = "https://www.investing.com/currencies/streaming-forex-rates-majors";
        param.market = MARKETS.CURRENCIES;
        try {
            provider.connect(param);
            List<DataNode> currPairs = provider.quotes(MARKETS.CURRENCIES);
            for (DataNode node : currPairs) {
                System.out.println(node.getRic() + "\t" + node.getBid() + "\t" + node.getAsk());
            }
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }

    }

    private static void testEuriborCurve() {
        EuriborRatesProvider provider = EuriborRatesProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.euribor-rates.eu/it/";
        param.url = "https://www.euribor-rates.eu/it/tassi-euribor-aggiornati/";
        param.extraParams.add("EURIBOR");

        param.market = MARKETS.YIELDS;
        try {
            provider.connect(param);
            org.softcaster.commons.types.Date today = new org.softcaster.commons.types.Date();
            provider.build(today);
            List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
            for (DataNode node : rates) {
                if (node instanceof YieldNode yieldNode) {
                    System.out.println(yieldNode.getRic() + "\t" + yieldNode.getMaturity() + "\t" + node.getBid());
                }
            }
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
        
    }
    
    private static void testYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.investing.com";
        param.url = "https://www.investing.com/rates-bonds/usa-government-bonds";
        //param.url = "https://www.investing.com/rates-bonds/italy-government-bonds";
        param.extraParams.add("USD");
        param.today = new org.softcaster.commons.types.Date();
        param.market = MARKETS.YIELDS;
        
        try {
            provider.refresh(param);
            List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
            for (DataNode node : rates) {
                if (node instanceof YieldNode yieldNode) {
                    System.out.println(yieldNode.getRic() + "\t" + yieldNode.getMaturity() + "\t" + node.getBid());
                }
            }
        } catch (MarketDataProviderException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public static void main(String[] args) {

        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();

        //testEuriborCurve();
        //testYieldCurves();

        testCurrencyPairs();
    }
}
