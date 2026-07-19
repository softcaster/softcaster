/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cnbc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import org.jsoup.Jsoup;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.Offset;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.cme.EsterOvn;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;
import static org.softcaster.provider.enums.Market.COMMODITIES;
import static org.softcaster.provider.enums.Market.CURRENCIES;
import static org.softcaster.provider.enums.Market.EQUITIES;
import static org.softcaster.provider.enums.Market.FUTURES;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.enums.OffsetType;
import org.softcaster.provider.exceptions.MarketDataProviderException;
import org.softcaster.provider.interpreter.ProviderHelper;

/**
 *
 * @author softc
 */
public class CnbcProvider extends AbstractProvider {

    private static CnbcProvider _instance = null;
    private CnbcApiClient client = null;

    private CnbcProvider() {
        client = new CnbcApiClient();
    }

    public static CnbcProvider getInstance() {
        if (_instance == null) {
            _instance = new CnbcProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case RATES ->
                parseResponseYieldCurve(info.getExtraParameters().get(0));
            case FUTURES, CURRENCIES, BONDS, EQUITIES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
        }
    }

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        customConnect(info, market);
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {

        parseResponseYieldCurve(info.getExtraParameters().get(0));
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        switch (idCurve) {
            case "ITYIELD" -> {
                return getItYieldCurveNodes();
            }
            case "USYIELD" -> {
                return getItYieldCurveNodes();
            }
        }
        return null;
    }

    private void parseResponseYieldCurve(String idCurve) {
        ProviderHelper helper = ProviderHelper.getInstance();
        if (helper != null) {
            try {
                ObjectMapper om = new ObjectMapper();
                List<Node> nodes = helper.getNodeList(idCurve);
                int pos = 0;
                double value = 0.;
                Data data = null;
                RateKey key = new RateKey(idCurve, RATES);
                for (Node node : nodes) {
                    try {
                        value = client.getRate(node.getSymbol());
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }

                    data = new Data(value / 100., value / 100.);
                    node.setData(data);
                    addRate(key, node);
                    pos++;
                }
                // Aggiungo tasso ester ovn
                String jsonResponse = Jsoup.connect("https://api.estr.dev/latest")
                        .ignoreContentType(true) // Obbligatorio per evitare errori con MIME type JSON
                        .execute()
                        .body();
                EsterOvn esterOvn = om.readValue(jsonResponse, EsterOvn.class);
                data = new Data(esterOvn.value / 100., esterOvn.value / 100.);
                Offset offset = new Offset(1, OffsetType.DAYS);
                Node nodeOvn = new Node("Ovn", offset, data, "ACT_360", "SIMPLE");
                addRate(key, nodeOvn);
            } catch (IOException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
            }
        }
    }

    private List<Node> getItYieldCurveNodes() {
        try {
            ProviderInfo info = new ProviderInfo();

            info.getExtraParameters().clear();
            info.getExtraParameters().add("ITYIELD");

            connect(info, RATES);

            RateKey key = new RateKey("ITYIELD", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

}
