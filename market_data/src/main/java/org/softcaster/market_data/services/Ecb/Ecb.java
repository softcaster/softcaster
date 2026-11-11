/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Ecb;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;

public class Ecb implements MarketDataManager {

    private EcbYieldClient client = null;
    private final List<Node> rates = new ArrayList<>();

    public Ecb(RequestParamManager rpm) {
        client = new EcbYieldClient();
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    @Override
    public List<Node> getRates(String curveId) {
        String rawJson = client.fetchFullCurve();
        EcbCurveParser parser = new EcbCurveParser();
        Map<String, Double> data = parser.parseAndFilterCurve(rawJson);

        data.forEach((String maturity, Double rate) -> {
            Node node = getNode(maturity, rate);
            rates.add(node);
        });

        return rates;
    }

    private Node getNode(String maturity, Double rate) {
        try {
            long step = Converter.toInt(maturity.split("-")[0]);
            String type = maturity.split("-")[1].split(" ")[0];
            OffsetType offsetType = OffsetType.NONE;
            switch (type) {
                case "year" ->
                    offsetType = OffsetType.YEARS;
                case "month" ->
                    offsetType = OffsetType.MONTHS;
                default -> {
                }
            }
            Offset offset = new Offset(step, offsetType);
            Data data = new Data(rate / 100., rate / 100.);
            return new Node(maturity, offset, DaycountBasis.ACT_365, Compounding.CONTINUOUS, CurveNodeType.ZERO_RATES, data);
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public String getMarketDataServiceId() {
        return "Ecb";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.RATES);
        return markets;
    }

}
