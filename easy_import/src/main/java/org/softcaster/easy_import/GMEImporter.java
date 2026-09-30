/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.types.DateParser;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.core.data.MarketQuote;
import org.softcaster.core.data.MarketQuoteDefinition;
import org.softcaster.core.data.MarketQuoteDefinitionDAO;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("GME Power Market Quotes")
public class GMEImporter implements IImportMgr {

    @Autowired
    private MarketQuoteDefinitionDAO marketQuoteDefinitionDAO;

    private final Map<String, List<MarketQuoteRecord>> marketQuotes = new HashMap<>();
    private MarketQuoteDefinition marketQuoteDefinition = null;

    @Override
    public void start(IProgressInfo progressInfo) {
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String fileName = paramsMgr.getParamValue("GME_MKT_QUOTES");
        Path path = Paths.get(IMPORT_PATH + "/" + fileName);

        CsvImport csvImport = new CsvImport();
        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8

        try {
            csvImport.startImport(config);
            DateParser parser = null;
            Date dt = null;
            String mqDefinition = "";
            int cnt = 0;
            double price = 0.;
            for (String[] s : csvImport.getBuffer()) {
                if (s[0].isEmpty()) {
                    System.out.println("Error: " + s[0].trim());
                    continue;
                }

                // Testata
                mqDefinition = s[0].trim();
                // Sostituisce il carattere BOM iniziale se presente
                mqDefinition = s[0].trim().replace("\uFEFF", "");
                // Data
                parser = new DateParser(s[1].trim());
                dt = new Date(parser.year(), parser.month(), parser.day());
                // Prezzo
                price = Converter.toDouble(s[2].trim(), false);

                // Se cambia la chiava crea una nuova lista. Ala fine aggiunge l'elemento alla lista
                marketQuotes.computeIfAbsent(mqDefinition, k -> new ArrayList<>())
                        .add(new MarketQuoteRecord(dt.sqlDate().toLocalDate(), price));
                cnt++;
            }

            insertOrUpdateMarketQuote(marketQuotes);
            progressInfo.updateProgress("Terminated", 100);
        } catch (Exception ex) {
            String error = ex.getLocalizedMessage();
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }

    }

    @Override
    public void terminate() {
    }

    private void insertOrUpdateMarketQuote(Map<String, List<MarketQuoteRecord>> marketQuotes) {

        marketQuotes.forEach((ticker, listaRecord) -> {
            for (MarketQuoteRecord record : listaRecord) {
                if (marketQuoteDefinition == null) {
                    marketQuoteDefinition = marketQuoteDefinitionDAO.findByCodeWithQuotes(ticker);
                }
                if (marketQuoteDefinition != null) {

                    final LocalDate businessDate = record.businessDate(); // Variabile finale per la lambda
                    Optional<MarketQuote> quoteEsistente = marketQuoteDefinition.getQuotes().stream()
                            .filter(q -> q.getBusinessDate().equals(businessDate))
                            .findFirst();

                    if (quoteEsistente.isPresent()) {
                        // SE ESISTE: Aggiorno solo il prezzo (Hibernate farà un UPDATE)
                        quoteEsistente.get().setPrice(BigDecimal.valueOf(record.price()));
                    } else {
                        // SE NON ESISTE: Creo un nuovo record (Hibernate farà un INSERT)
                        MarketQuote newQuote = new MarketQuote();
                        newQuote.setBusinessDate(businessDate);
                        newQuote.setPrice(BigDecimal.valueOf(record.price()));

                        marketQuoteDefinition.getQuotes().add(newQuote);
                    }
                }
            }
            marketQuoteDefinitionDAO.saveOrUpdate(marketQuoteDefinition);
        });
    }

    private record MarketQuoteRecord(LocalDate businessDate, double price) {
    }
}
