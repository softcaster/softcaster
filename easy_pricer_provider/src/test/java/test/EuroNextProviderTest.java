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
        /*
        param.baseUrl = "https://www.borsaitaliana.it";
        param.url = "https://www.borsaitaliana.it/borsa/obbligazioni/mot/btp/scheda/";
        param.extraParams.add("IT0001086567");
        param.extraParams.add("-MOTX.html?lang=en"); //-DMIL future -ETLX equity
        param.market = MARKETS.BONDS;
         */
        param.baseUrl = "https://live.euronext.com/en";
        param.extraParams.add("IT0001086567");
        param.extraParams.add("-MOTX");
        param.market = MARKETS.BONDS;

        provider.refresh(param);
        List<DataNode> rates = provider.quotes(MARKETS.BONDS);
        for (DataNode node : rates) {
            System.out.println(node.getRic() + "\t" + "\t" + node.getBid());
        }

        param.baseUrl = "https://live.euronext.com/en";
        param.extraParams.clear();
        param.extraParams.add("IT0024832682");
        param.extraParams.add("MBTP-DMIL?fOrO=F&md=01-06-2026");
        param.market = MARKETS.FUTURES;

        provider.refresh(param);
        rates = provider.quotes(MARKETS.FUTURES);
        for (DataNode node : rates) {
            System.out.println(node.getRic() + "\t" + "\t" + node.getBid());
        }

    }
}
