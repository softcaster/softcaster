/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package investing_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.cnbc.CnbcProvider;
import org.softcaster.provider.ecb.ECBProvider;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.eodhd.EodhdFxApiClient;
import org.softcaster.provider.eurex.EurexProvider;
import org.softcaster.provider.frankfurter.FrankfurterProvider;
import org.softcaster.provider.investing.InvestingComProvider;
import org.softcaster.provider.twelvedata.TwelvedataProvider;
import org.softcaster.provider.yahoo.YahooApiClient;

/**
 *
 * @author ep
 */
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.softcaster.provider.euronext.BorsaItalianaProvider;

class Root {

    public ArrayList<ArrayList<Double>> d;
}

class BorsaItalianaJsonClient {

    public void testBI2(String symbol, String market) {
        // 1. URL esatto 
        //String endpointUrl = "https://grafici.borsaitaliana.it/api/instruments/IT0025480929,DMIL,ISIN/intraday?resolution=1MN";
        String endpointUrl = "https://grafici.borsaitaliana.it/api/instruments/IT0001278511,XMIL,ISIN/intraday?resolution=1MN";
        // Token autenticazione
        String bearerToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiItMSIsImV4cCI6NDkwNTU4MzU3MiwiaWF0IjoxNzUxOTgzNTcyLCJhdXRob3JpdGllcyI6W119.d7Eh_LOGqA44BH58HIiPrPIz1SLskVOPj4BRsae05cI";

        HttpClient client = HttpClient.newHttpClient();

        // 3. Costruzione della richiesta GET configurando gli header visibili in figura
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Accept", "application/json")
                // Inserimento del token di autenticazione Bearer obbligatorio
                .header("Authorization", "Bearer " + bearerToken)
                // Inseriamo Referer e User-Agent per evitare blocchi dal WAF del sito
                //.header("Referer", "https://grafici.borsaitaliana.it/summary-chart/IT0025480929-XDMI?lang=it")
                .header("Referer", "https://grafici.borsaitaliana.it/summary-chart/IT0001278511-MOTX?lang=it")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        try {
            System.out.println("Invio richiesta a grafici.borsaitaliana.it...");
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Risposta JSON ricevuta:");
            System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("Errore durante la chiamata:");
            e.printStackTrace();
        }
    }

    public void testBI(String symbol, String market) {

        // Endpoint interno di Borsa Italiana per i servizi grafici/prezzi
        String endpointUrl = "https://charts.borsaitaliana.it/charts/services/ChartWService.asmx/GetPrices";

        // Inserisci l'ISIN del tuo BTP Italia seguito da .MOT (es. IT0001086567.MOT)
        String btpKey = symbol + "." + market;

        // Costruzione del payload JSON richiesto dal servizio .asmx
        String jsonPayload = "{"
                + "\"request\": {"
                + "  \"SampleTime\": \"1mm\","
                + "  \"TimeFrame\": \"1d\","
                + "  \"RequestedDataSetType\": \"ohlc\","
                + "  \"ChartPriceType\": \"price\","
                + "  \"Key\": \"" + btpKey + "\","
                + "  \"OffSet\": 0,"
                + "  \"FromDate\": null,"
                + "  \"ToDate\": null,"
                + "  \"UseRealTime\": true"
                + "}"
                + "}";

        // Creazione del client HTTP standard
        HttpClient client = HttpClient.newHttpClient();

        // Configurazione della richiesta con gli header corretti
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpointUrl))
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)") // Evita blocchi base simulando un browser
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            System.out.println("Richiesta dati JSON per il BTP: " + btpKey);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Stampa dello stato e del contenuto JSON restituito
            System.out.println("HTTP Status Code: " + response.statusCode());
            System.out.println("Risposta JSON ricevuta:");
            ObjectMapper om = new ObjectMapper();
            Root root = om.readValue(response.body(), Root.class);
            System.out.println(root.d.get(0));
            //System.out.println(response.body());

        } catch (Exception e) {
            System.err.println("Errore durante il recupero dei dati:");
            e.printStackTrace();
        }
    }
}

public class InvestingComTest {

    private static void testCurrencyPairs() {
        TwelvedataProvider provider = TwelvedataProvider.getInstance();
        Node node = provider.getMktQuote("EURUSD", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURCHF", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURJPY", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURCAD", Market.CURRENCIES);
        System.out.println(node.getData().bid());

        node = provider.getMktQuote("EURAUD", Market.CURRENCIES);
        System.out.println(node.getData().bid());
    }

    private static void testItaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getItYieldCurve();
        for (Node n : nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    private static void testUsaYieldCurves() {
        InvestingComProvider provider = InvestingComProvider.getInstance();

        List<Node> nodes = provider.getUsYieldCurve();
        for (Node n : nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        /*
        System.out.println("########## TwelvedataProvider ##########");
        testCurrencyPairs();
        */
        //testEcbClient();
        testEodhdFxApiClient();
        //System.out.println("########## IT Yield Curve ##########");
        // testItaYieldCurves();
        /*
        System.out.println("");
        System.out.println("########## US Yield Curve ##########");
        testUsaYieldCurves();
         */
 /*
        CnbcProvider provider = CnbcProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("USYIELD");
        for(Node node:nodes) {
            System.out.println(node.getSymbol() + " " + node.getData().bid());
        }
         */
        //testEcbClient();
        //testCurrencyPairs();
        //testEurexClient();
        /*
        System.out.println("########## YahooProvider ##########");
        testYahooClient();
        */
        //testBIProvider();
    }

    private static void testEodhdFxApiClient() {
        EodhdFxApiClient client = new EodhdFxApiClient();
        System.out.println(client.getRealTimeExchangeRate("EURUSD.FOREX"));

        FrankfurterProvider provider = FrankfurterProvider.getInstance();
        System.out.println(provider.getMktQuote("EURUSD", Market.CURRENCIES).getData().bid());
    }

    private static void testEcbClient() {

        ECBProvider provider = ECBProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("EcbYiedCurve");

        for (Node node : nodes) {
            System.out.println(node.getSymbol() + ":" + node.getData().bid());
        }
    }

    private static void testEurexClient() {
        EurexProvider provider = EurexProvider.getInstance();
        Node node = provider.getMktQuote("AAA", Market.FUTURES);
        if (node != null) {
            System.out.println(node.getData().bid());
        }
    }

    private static void testBIProvider() {
        BorsaItalianaProvider provider = BorsaItalianaProvider.getInstance();

        System.out.println(provider.getMktQuote("IT0004545890", Market.BONDS).getData().bid());
        System.out.println(provider.getMktQuote("IT0025480929", Market.FUTURES).getData().bid());

    }
    private static void testYahooClient() {
        YahooApiClient client = new YahooApiClient();
        try {
            System.out.println(client.getRate("EURUSD"));
            System.out.println(client.getRate("EURCHF"));
            System.out.println(client.getRate("EURJPY"));
            System.out.println(client.getRate("EURCAD"));
            System.out.println(client.getRate("EURAUD"));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(InvestingComTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
        BorsaItalianaApiClient client2 = new BorsaItalianaApiClient();
        System.out.println(client2.getQuote("IT0001278511", Market.BONDS));
        System.out.println(client2.getQuote("IT0025480929", Market.FUTURES));
        */
    }
}
