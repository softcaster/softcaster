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
import org.softcaster.marketdataprovider.euronext.EuroNextProvider;

/**
 *
 * @author ep
 */
public class EuroNextProviderTest {
    
    public static void main(String[] args) {

        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();

        EuroNextProvider provider = EuroNextProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://live.euronext.com/en/";
        param.url = "https://live.euronext.com/en/ajax/getDetailedQuote/";
        param.extraParams.add("IT0005494239");
        param.extraParams.add("-MOTX"); //-DMIL future -ETLX equity
        param.market = MARKETS.BONDS;

        provider.refresh(param);
        List<DataNode> rates = provider.quotes(MARKETS.BONDS);
        for (DataNode node : rates) {
            System.out.println(node.getRic() + "\t" + "\t" + node.getBid());
        }
    }}
