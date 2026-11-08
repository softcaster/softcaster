/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Eurext;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.RequestParamManager;

public class EurexMDM implements MarketDataManager {

    RequestParamManager rpm = null;

    public EurexMDM(RequestParamManager rpm) {
        this.rpm = rpm;
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "EurexMDS";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.FUTURES);
        markets.add(Market.OPTIONS);
        return markets;
    }

    @Override
    public void addParam(String param) {
        rpm.add(param);
    }

    @Override
    public String getParam(int pos) {
        return rpm.get(pos);
    }
}
