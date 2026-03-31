/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.util.List;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderHelper;
import org.softcaster.marketdataprovider.YieldNode;
import org.softcaster.marketdataprovider.sole24h.Sole24hProvider;

/**
 *
 * @author softc
 */
public class TestClass {

    private static void testSole24hProvider(){
        
        Sole24hProvider provider = Sole24hProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.ilsole24ore.com/";
        param.url = "https://mercatiwdg.ilsole24ore.com/FinanzaMercati/WidgetSelector/listino?widgetConfiguration=FMIRS";
        param.extraParams.add("EURIRS");
        param.market = MARKETS.YIELDS;

        provider.refresh(param);
        List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
        for (DataNode node : rates) {
            if (node instanceof YieldNode yieldNode) {
                System.out.println(yieldNode.getRic() + "\t" + yieldNode.getMaturity() + "\t" + node.getBid());
            }
        }
    }
    
    public static void main(String[] args) {
        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();
        
        testSole24hProvider();
    }
}
