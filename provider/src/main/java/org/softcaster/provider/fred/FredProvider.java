/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.fred;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author ep
 */
public class FredProvider extends AbstractProvider {

    private static FredProvider _instance = null;

    public static FredProvider getInstance() {
        if (_instance == null) {
            _instance = new FredProvider();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
