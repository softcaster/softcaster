/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Eurext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDate;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.Node;

public class EurexApiClient {

    private static final String ENDPOINT
            = "https://api.developer.deutsche-boerse.com/eurex-prod-graphql/";
    private static final String API_KEY = "68cdafd2-c5c1-49be-8558-37244ab4f513";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    // Un solo contratto (future o serie di opzione) filtrato per ISIN lato server.
    // Contracts riporta gia' l'ultimo settlement: SettlementPrices darebbe
    // qualche giorno di storico per ogni contratto del prodotto.
    // Senza paginazione l'API richiede sempre Product: { eq: ... }.
    private static final String QUERY_CONTRACT_PRICE = """
            query($product: String, $isin: String) {
              contracts: Contracts(filter: { Product: { eq: $product }, ISIN: { eq: $isin } }) {
                data { ContractID ISIN SettlementDate SettlementPrice }
              }
            }
            """;

    // Serie di opzione per scadenza, strike e call/put: l'ISIN identifica la singola serie
    private static final String QUERY_OPTION_ISIN = """
            query($product: String, $expiry: Date, $strike: Float, $callPut: String) {
              contracts: Contracts(filter: {
                Product: { eq: $product },
                ExpirationDate: { eq: $expiry },
                Strike: { eq: $strike },
                CallPut: { eq: $callPut }
              }) {
                data { ISIN VersionNr }
              }
            }
            """;

    private static final String QUERY_DELIVERABLES = """
            query($product: String) {
              deliverables: DeliverableBonds(filter: { Product: { eq: $product } }) {
                date
                data { ContractID BondISIN BondName BondCouponRate BondMaturityDate ConversionFactor }
              }
            }
            """;

    private String product;

    public EurexApiClient() {
    }

    Node getOptionQuote(String isin) {
        return getQuote(isin);
    }

    public Node getFutureQuote(String isin) {
        return getQuote(isin);
    }

    private Node getQuote(String isin) {
        try {
            ContractData d = executeQueryContractPrice(isin);
            if (d == null) {
                return null;
            }
            return new Node(d.isin(),
                    null,
                    null, // DaycountBasis
                    null, // Compounding
                    null, // CurveNodeType
                    new Data(d.price(), d.price()));
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Ultimo prezzo di settlement del contratto con l'ISIN dato, null se il
     * prodotto non ha quell'ISIN o il contratto non ha ancora un settlement.
     * @param isin
     * @return 
     * @throws java.lang.Exception
     */
    public ContractData executeQueryContractPrice(String isin) throws Exception {
        JsonNode data = execute(QUERY_CONTRACT_PRICE, Map.of("product", product, "isin", isin));
        for (JsonNode c : data.path("contracts").path("data")) {
            if (c.path("SettlementPrice").isNull() || c.path("SettlementPrice").isMissingNode()) {
                continue;
            }
            return new ContractData(
                    c.get("ContractID").asInt(),
                    c.path("ISIN").asText(),
                    c.path("SettlementDate").asText(),
                    c.path("SettlementPrice").asDouble());
        }
        return null;
    }

    /**
     * ISIN della serie di opzione, null se la serie non esiste.
     *
     * @param product codice Eurex del prodotto, es. OESX
     * @param expiry data di scadenza della serie, es. 2026-12-18
     * @param strike prezzo di esercizio
     * @param callPut "C" o "P"
     * @return 
     * @throws java.lang.Exception
     */
    public static String findIsin(String product, LocalDate expiry, double strike, String callPut) throws Exception {
        Map<String, Object> variables = Map.of(
                "product", product,
                "expiry", expiry.toString(),
                "strike", strike,
                "callPut", callPut.toUpperCase());
        JsonNode data = execute(QUERY_OPTION_ISIN, variables);

        // dopo un aggiustamento di capitale possono convivere piu' versioni della
        // stessa serie: la standard e' quella con VersionNr piu' basso
        String isin = null;
        int version = Integer.MAX_VALUE;
        for (JsonNode c : data.path("contracts").path("data")) {
            int v = c.path("VersionNr").asInt();
            if (v < version) {
                version = v;
                isin = c.path("ISIN").asText();
            }
        }
        return isin;
    }

    /**
     * Catena delle opzioni di una scadenza, ordinata per strike e poi call/put.
     *
     * @param product codice Eurex del prodotto, es. OESX
     * @param expiry data di scadenza, es. 2026-12-18
     * @return tutte le serie della scadenza
     * @throws java.lang.Exception
     */
    public static List<OptionSeries> findSeries(String product, LocalDate expiry) throws Exception {
        return findSeries(product, expiry, null, null, null);
    }

    /**
     * Catena delle opzioni di una scadenza, filtrata lato server. I filtri a
     * null non vengono applicati. Se una serie ha piu' versioni (aggiustamento
     * di capitale) le restituisce tutte: quella standard ha VersionNr piu' basso.
     *
     * @param product codice Eurex del prodotto, es. OESX
     * @param expiry data di scadenza, es. 2026-12-18
     * @param strikeMin strike minimo incluso, null per nessun limite
     * @param strikeMax strike massimo incluso, null per nessun limite
     * @param callPut "C" o "P", null per entrambe
     * @return le serie ordinate per strike e poi call/put
     * @throws java.lang.Exception
     */
    public static List<OptionSeries> findSeries(String product, LocalDate expiry,
            Double strikeMin, Double strikeMax, String callPut) throws Exception {
        return findSeries(product, expiry, expiry, strikeMin, strikeMax, callPut);
    }

    /**
     * Catene di tutte le scadenze comprese tra due date (incluse), ordinate per
     * scadenza, strike e call/put. Serve, per esempio, a scaricare in una sola
     * chiamata i prezzi per una superficie di volatilita'.
     *
     * @param product codice Eurex del prodotto, es. OESX
     * @param from prima scadenza inclusa
     * @param to ultima scadenza inclusa
     * @return le serie delle scadenze nell'intervallo
     * @throws java.lang.Exception
     */
    public static List<OptionSeries> findSeriesBetween(String product, LocalDate from, LocalDate to) throws Exception {
        return findSeries(product, from, to, null, null, null);
    }

    private static List<OptionSeries> findSeries(String product, LocalDate from, LocalDate to,
            Double strikeMin, Double strikeMax, String callPut) throws Exception {
        // il filtro si compone con i soli criteri presenti: un eq/ge a null
        // non equivale a "nessun filtro"
        StringBuilder params = new StringBuilder("$product: String, $from: Date, $to: Date");
        StringBuilder filter = new StringBuilder("Product: { eq: $product }, ExpirationDate: { ge: $from, le: $to }");
        Map<String, Object> variables = new HashMap<>();
        variables.put("product", product);
        variables.put("from", from.toString());
        variables.put("to", to.toString());
        if (strikeMin != null) {
            params.append(", $strikeMin: Float");
            filter.append(", Strike: { ge: $strikeMin");
            variables.put("strikeMin", strikeMin);
        }
        if (strikeMax != null) {
            params.append(", $strikeMax: Float");
            filter.append(strikeMin != null ? ", le: $strikeMax" : ", Strike: { le: $strikeMax");
            variables.put("strikeMax", strikeMax);
        }
        if (strikeMin != null || strikeMax != null) {
            filter.append(" }");
        }
        if (callPut != null) {
            params.append(", $callPut: String");
            filter.append(", CallPut: { eq: $callPut }");
            variables.put("callPut", callPut.toUpperCase());
        }
        String query = "query(" + params + ") { contracts: Contracts(filter: { " + filter + " }) {"
                + " data { ContractID ISIN ExpirationDate Strike CallPut VersionNr"
                + " SettlementDate SettlementPrice OpenInterest } } }";

        JsonNode data = execute(query, variables);
        List<OptionSeries> series = new ArrayList<>();
        for (JsonNode c : data.path("contracts").path("data")) {
            JsonNode price = c.path("SettlementPrice");
            series.add(new OptionSeries(
                    c.path("ISIN").asText(),
                    c.path("ContractID").asInt(),
                    c.path("ExpirationDate").asText(),
                    c.path("Strike").asDouble(),
                    c.path("CallPut").asText(),
                    c.path("VersionNr").asInt(),
                    c.path("SettlementDate").asText(),
                    price.isNull() || price.isMissingNode() ? null : price.asDouble(),
                    c.path("OpenInterest").asLong()));
        }
        series.sort(Comparator.comparing(OptionSeries::expirationDate)
                .thenComparingDouble(OptionSeries::strike)
                .thenComparing(OptionSeries::callPut)
                .thenComparingInt(OptionSeries::versionNr));
        return series;
    }

    public Map<Integer, List<DeliverableBond>> executeQueryDeliverables() throws Exception {
        JsonNode data = execute(QUERY_DELIVERABLES, Map.of("product", product));

        Map<Integer, List<DeliverableBond>> basket = new HashMap<>();
        for (JsonNode b : data.path("deliverables").path("data")) {
            DeliverableBond bond = new DeliverableBond(
                    b.get("ContractID").asInt(),
                    b.path("BondISIN").asText(),
                    b.path("BondName").asText(),
                    b.path("BondCouponRate").asDouble(),
                    b.path("BondMaturityDate").asText(),
                    b.path("ConversionFactor").asDouble());
            basket.computeIfAbsent(bond.contractId(), k -> new ArrayList<>()).add(bond);
        }
        basket.values().forEach(l -> l.sort(Comparator.comparing(DeliverableBond::maturity)));
        return basket;
    }

    private static JsonNode execute(String query, Map<String, Object> variables) throws Exception {
        String body = MAPPER.writeValueAsString(Map.of("query", query, "variables", variables));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .header("Content-Type", "application/json")
                .header("X-DBP-APIKEY", API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = MAPPER.readTree(response.body());
        if (root.has("errors")) {
            throw new RuntimeException("GraphQL error: " + root.get("errors"));
        }
        return root.path("data");
    }

    public void setProduct(String product) {
        this.product = product;
    }
}
