/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.MutuiIt;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;

record Result(String symbol, double value) {

}

public class MutuiIt implements MarketDataManager {

    public MutuiIt() {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    @Override
    public List<Node> getRates(String curveId) {
        try {
            Document doc = Jsoup.connect("https://www.mutui.it/guida-mutui/tasso-irs-mutui-come-funziona.html").get();

            // 3. Seleziona tutti i tag <script>
            Elements scripts = doc.select("script");

            // Forza la chiusura immediata alla prima sequenza di "]]}"
            // ed esclude esplicitamente le altre label (Data, Fixing Pr., ecc.)
            String regex = "(\\{\"label\":\"(Descrizione|Fixing)\",\"values\":\\[\\[.*?\\]\\]\\})";
            Pattern pattern = Pattern.compile(regex);

            String jsonDescrizione = null;
            String jsonFixing = null;

            for (Element script : scripts) {
                String scriptText = script.data();
                Matcher matcher = pattern.matcher(scriptText);

                while (matcher.find()) {
                    String found = matcher.group(1);
                    String tipoLabel = matcher.group(2); // Cattura "Descrizione" o "Fixing"

                    if ("Descrizione".equals(tipoLabel)) {
                        jsonDescrizione = found;
                    } else if ("Fixing".equals(tipoLabel)) {
                        jsonFixing = found;
                    }
                }
            }
            ObjectMapper om = new ObjectMapper();
            Root rootFixing = om.readValue(jsonFixing, Root.class);
            Root rootDescription = om.readValue(jsonDescrizione, Root.class);
            List<Result> resultList = getResultList(rootDescription, rootFixing);
            if (resultList != null && !resultList.isEmpty()) {
                for (Result result : resultList) {
                    System.out.println(result.symbol() + "\t" + result.value());
                }
            }
            return null;
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public String getMarketDataServiceId() {
        return "MutuiIt";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.RATES);
        return markets;
    }

    private List<Result> getResultList(Root rootDescription, Root rootFixing) {
        List<Result> resultList = null;

        if (rootDescription != null && rootDescription.values != null && !rootDescription.values.isEmpty()
                && rootFixing != null && rootFixing.values != null && !rootFixing.values.isEmpty()) {

            resultList = new ArrayList<>();

            // Determiniamo il numero di righe da ciclare (usiamo la dimensione minore per evitare IndexOutOfBoundsException)
            int size = Math.min(rootDescription.values.size(), rootFixing.values.size());

            for (int i = 0; i < size; i++) {
                // Estraiamo le liste interne (le righe) alla stessa posizione 'i'
                ArrayList<String> descRow = rootDescription.values.get(i);
                ArrayList<String> fixingRow = rootFixing.values.get(i);

                // Verifichiamo che le righe interne contengano effettivamente il valore (indice 0)
                if (!descRow.isEmpty() && !fixingRow.isEmpty()) {
                    try {
                        String descrizione = descRow.get(0);
                        String fixing = fixingRow.get(0);
                        double fixingValue = Converter.toDouble(fixing, false);
                        // Creiamo l'oggetto Result e lo aggiungiamo alla lista
                        Result result = new Result(descrizione, fixingValue);
                        resultList.add(result);
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        return null;
                    }
                }
            }
        }
        return resultList;
    }
}
