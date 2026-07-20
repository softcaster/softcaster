/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.ecb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EcbCurveParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Regex 1: Identifica gli anni interi (es: "Yield curve spot rate, 10-year maturity")
    private final Pattern regexAnniInteri = Pattern.compile(
            "^Yield curve spot rate, \\d+-year maturity$"
    );

    // Regex 2: Identifica i mesi brevi puri (es: "Yield curve spot rate, 3-month maturity")
    private final Pattern regexMesiPuri = Pattern.compile(
            "^Yield curve spot rate, \\d+-month maturity$"
    );

    /**
     * Parsa il JSON della BCE e restituisce una mappa filtrata solo con anni
     * interi e mesi puri.
     * @param fullJsonFromEcb
     * @return 
     */
    public Map<String, Double> parseAndFilterCurve(String fullJsonFromEcb) {
        Map<String, Double> filteredCurve = new LinkedHashMap<>();

        try {
            JsonNode root = objectMapper.readTree(fullJsonFromEcb);

            // 1. Recupera il dizionario dei metadati delle scadenze
            JsonNode valuesNode = root.path("structure")
                    .path("dimensions")
                    .path("series")
                    .get(6)
                    .path("values");

            // 2. Entra nel blocco dei dati reali
            JsonNode seriesNode = root.path("dataSets").get(0).path("series");

            var fields = seriesNode.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                String key = field.getKey(); // Es: "0:0:0:0:0:0:6"
                JsonNode content = field.getValue();

                // Estrae l'indice posizionale della scadenza
                String[] parts = key.split(":");
                int maturityIndex = Integer.parseInt(parts[parts.length - 1]);

                if (maturityIndex < valuesNode.size()) {
                    String maturityName = valuesNode.get(maturityIndex).path("name").asText().trim();

                    // APPLICAZIONE DEI FILTRI:
                    // Deve essere uno "spot rate" ed essere un ANNO INTERO oppure un MESE PURO
                    boolean isAnnoIntero = regexAnniInteri.matcher(maturityName).matches();
                    boolean isMesePuro = regexMesiPuri.matcher(maturityName).matches();

                    if (isAnnoIntero || isMesePuro) {
                        // Preleva il valore del tasso percentuale
                        JsonNode observations = content.path("observations");
                        if (observations.size() > 0) {
                            JsonNode firstObs = observations.elements().next();
                            double yieldValue = firstObs.get(0).asDouble();

                            // Formatta la chiave in modo più leggibile (opzionale, es: "10-year maturity")
                            String cleanKey = maturityName.replace("Yield curve spot rate, ", "");

                            filteredCurve.put(cleanKey, yieldValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nel parsing filtrato: " + e.getMessage());
        }

        return filteredCurve;
    }
}
