/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.eurex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.AbstractProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class EurexProvider  extends AbstractProvider {

    private static EurexProvider _instance = null;

    private EurexProvider() {
    }

    public static EurexProvider getInstance() {
        if (_instance == null) {
            _instance = new EurexProvider();
        }

        return _instance;
    }
    
    @Override
    public void refresh(ConnectionParam param) throws MarketDataProviderException {
        try {
            connect(param);
            build(param.today);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
        if (response != null && !response.isEmpty()) {
            String[] valuesStr = response.split("futures_futures_full_orderbook_15\">")[1].split("</span>");
            System.out.println(valuesStr[0]);
        }
    }

    @Override
    protected void customConnect(ConnectionParam param) throws MalformedURLException, IOException {
        param.useBaseUrl = false;
        HttpURLConnection conn = getConnection(param);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())))) {
            Stream<String> lines = br.lines();
            // Reset ultima richiesta
            response = "";
            // Impacca tutte le linee
            Consumer<String> addElement = s -> {
                response += s;
            };
            lines.forEach(addElement);
            // Chiusura buffer
            br.close();

            parseResponse(param);
        }
    }

    @Override
    public List<DataNode> quotes(MARKETS market) {
        return null;
    }
    
}
