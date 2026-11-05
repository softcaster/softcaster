/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.eurex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.*;
import java.util.*;

public class EurexGraphQL {

    private static final String ENDPOINT
            = "https://api.developer.deutsche-boerse.com/eurex-prod-graphql/";
    private static final String API_KEY = "68cdafd2-c5c1-49be-8558-37244ab4f513";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    record ContractInfo(int contractId, String contract, String isin, String expirationDate) {

    }

    record Settlement(int contractId, String settlementDate, double price,
            String priceType, String contractType) {

    }

    public static void main(String[] args) throws Exception {
        String product = args.length > 0 ? args[0] : "FBEU";

        String query = """
            query($product: String) {
              prezzi: SettlementPrices(filter: { Product: { eq: $product } }) {
                date
                data { ContractID SettlementDate SettlementPrice PriceType ContractType }
              }
              contratti: Contracts(filter: { Product: { eq: $product } }) {
                data { ContractID Contract ISIN ExpirationDate }
              }
            }
            """;

        JsonNode data = execute(query, Map.of("product", product));

        // Mappa ContractID -> anagrafica contratto
        Map<Integer, ContractInfo> contracts = new HashMap<>();
        for (JsonNode c : data.path("contratti").path("data")) {
            contracts.put(c.get("ContractID").asInt(), new ContractInfo(
                    c.get("ContractID").asInt(),
                    c.path("Contract").asText(),
                    c.path("ISIN").asText(),
                    c.path("ExpirationDate").asText()));
        }

        List<Settlement> prices = new ArrayList<>();
        for (JsonNode p : data.path("prezzi").path("data")) {
            prices.add(new Settlement(
                    p.get("ContractID").asInt(),
                    p.path("SettlementDate").asText(),
                    p.path("SettlementPrice").asDouble(),
                    p.path("PriceType").asText(),
                    p.path("ContractType").asText()));
        }
        prices.sort(Comparator.comparing(Settlement::settlementDate)
                .thenComparing(s -> contracts.containsKey(s.contractId())
                ? contracts.get(s.contractId()).expirationDate() : ""));

        System.out.printf("Settlement %s (riferimento: %s)%n%n",
                product, data.path("prezzi").path("date").asText());
        System.out.printf("%-12s %-28s %-14s %-12s %10s %s%n",
                "Data", "Contratto", "ISIN", "Scadenza", "Prezzo", "Tipo");

        for (Settlement s : prices) {
            ContractInfo c = contracts.get(s.contractId());
            System.out.printf("%-12s %-28s %-14s %-12s %10.3f %s%n",
                    s.settlementDate(),
                    c != null ? c.contract() : "ID " + s.contractId(),
                    c != null ? c.isin() : "-",
                    c != null ? c.expirationDate() : "-",
                    s.price(), s.priceType());
        }
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
        if (root.has("errors")) {  // GraphQL segnala gli errori anche con HTTP 200
            throw new RuntimeException("Errore GraphQL: " + root.get("errors"));
        }
        return root.path("data");
    }
}
