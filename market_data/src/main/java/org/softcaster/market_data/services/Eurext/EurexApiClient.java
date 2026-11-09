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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.Node;

public class EurexApiClient {

    private static final String ENDPOINT
            = "https://api.developer.deutsche-boerse.com/eurex-prod-graphql/";
    private static final String API_KEY = "68cdafd2-c5c1-49be-8558-37244ab4f513";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private static final String QUERY_PRICES = """
            query($product: String) {
              prices: SettlementPrices(filter: { Product: { eq: $product } }) {
                date
                data { ContractID SettlementDate SettlementPrice PriceType ContractType }
              }
              contracts: Contracts(filter: { Product: { eq: $product } }) {
                data { ContractID Contract ISIN ExpirationDate }
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

    private final String product;

    public EurexApiClient(String product) {
        this.product = Objects.requireNonNull(product, "product");
    }

    public Node getFutureQuote(String ticker) {
        try {
            Node node = null;
            List<ContractData> data = executeQueryIsinPrices();
            if (data != null && !data.isEmpty()) {
                for (ContractData d : data) {
                    if (d.isin().equals(ticker)) {
                        node = new Node(d.isin(),
                                null,
                                null, // DaycountBasis
                                null, // Compounding
                                null, // CurveNodeType
                                new Data(d.price(), d.price()));
                        break;
                    }
                }
            }
            return node;
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
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

    public List<ContractData> executeQueryIsinPrices() throws Exception {
        JsonNode data = execute(QUERY_PRICES, Map.of("product", product));

        Map<Integer, ContractInfo> contracts = new HashMap<>();
        for (JsonNode c : data.path("contracts").path("data")) {
            contracts.put(c.get("ContractID").asInt(), new ContractInfo(
                    c.get("ContractID").asInt(),
                    c.path("Contract").asText(),
                    c.path("ISIN").asText(),
                    c.path("ExpirationDate").asText()));
        }

        List<SettlementData> prices = new ArrayList<>();
        for (JsonNode p : data.path("prices").path("data")) {
            prices.add(new SettlementData(
                    p.get("ContractID").asInt(),
                    p.path("SettlementDate").asText(),
                    p.path("SettlementPrice").asDouble(),
                    p.path("PriceType").asText(),
                    p.path("ContractType").asText()));
        }
        prices.sort(Comparator.comparing(SettlementData::settlementDate)
                .thenComparing(s -> contracts.containsKey(s.contractId())
                ? contracts.get(s.contractId()).expirationDate() : ""));

        List<ContractData> result = new ArrayList<>();
        for (SettlementData s : prices) {
            ContractInfo c = contracts.get(s.contractId());
            result.add(new ContractData(
                    s.contractId(),
                    c != null ? c.isin() : "-",
                    c != null ? c.expirationDate() : "-",
                    s.price()));
        }
        return result;
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
            throw new RuntimeException("Errore GraphQL: " + root.get("errors"));
        }
        return root.path("data");
    }
}
