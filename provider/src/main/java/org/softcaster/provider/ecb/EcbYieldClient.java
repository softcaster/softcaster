/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.ecb;

import org.springframework.web.client.RestClient;
import java.net.URI;

public class EcbYieldClient {

    private final RestClient client = RestClient.builder().build();

    /**
     * Recupera l'intera struttura dei tassi (Yield Curve) dell'ultimo giorno feriale.
     * @return 
     */
    public String fetchFullCurve() {
        // WILDCARD: Lasciando vuoto l'ultimo parametro dopo il punto, chiediamo TUTTE le scadenze (3M, 6M, 1Y, 10Y...)
        String wildcardKey = "B.U2.EUR.4F.G_N_A.SV_C_YM.";
        
        String protocol = "https://";
        String subdomain = "data-api";
        String domain = ".ecb.eu" + "ro" + "pa.eu";
        String endpoint = "/service/data/YC/"; 
        
        //lastNObservations=1 prende l'ultimo giorno feriale disponibile per ogni singola scadenza della curva
        String fullUrl = protocol + subdomain + domain + endpoint + wildcardKey + "?lastNObservations=1";
        
        URI targetUri = URI.create(fullUrl);

        try {
            return client.get()
                    .uri(targetUri)
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Errore di connessione: " + e.getMessage();
        }
    }
}
