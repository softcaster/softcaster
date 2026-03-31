/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.util.List;
import org.softcaster.marketdataprovider.CmeGroup.CmeGroupProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderHelper;

/**
 *
 * @author softc
 */
public class CmeProviderTest {

    private static void testCme() {
        CmeGroupProvider provider = CmeGroupProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        /*
        param.baseUrl = "https://www.cmegroup.com";
        param.url = "https://www.cmegroup.com/services/sofr-strip-rates/";
        param.extraParams.add("SOFR");
        param.market = MARKETS.YIELDS;
        
        provider.refresh(param);
        List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
        for (DataNode node : rates) {
            if (node instanceof YieldNode yieldNode) {
                System.out.println(yieldNode.getRic() + "\t" + yieldNode.getMaturity() + "\t" + node.getBid());
            }
        }
        */
        // M6EM6-2674; SR3M6-8462
        provider = CmeGroupProvider.getInstance();
        param = new ConnectionParam();
        param.baseUrl = "https://www.cmegroup.com";
        param.url = "https://www.cmegroup.com/CmeWS/mvc/quotes/v2/";
        param.extraParams.add("8462");
        param.market = MARKETS.FUTURES;

        provider.refresh(param);
        List<DataNode> rates = provider.quotes(MARKETS.FUTURES);
        for (DataNode node : rates) {
                System.out.println(node.getRic() + "\t"  + node.getBid());
        }
    }

    public static void main(String[] args) {

        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();

        testCme();
    }
}
