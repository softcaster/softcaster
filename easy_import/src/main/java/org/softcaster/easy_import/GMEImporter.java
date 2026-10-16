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
import org.softcaster.core.data.PowerSpotPrice;
import org.softcaster.core.data.PowerSpotPriceDefinition;
import org.softcaster.core.data.PowerSpotPriceDefinitionDAO;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("GME Power Market Quotes")
public class GMEImporter implements IImportMgr {

    @Autowired
    private PowerSpotPriceDefinitionDAO powerSpotPriceDefinitionDAO;

    @Override
    public void start(IProgressInfo progressInfo) {
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String fileName = paramsMgr.getParamValue("GME_POWER_SPOT");
        Path path = Paths.get(IMPORT_PATH + "/" + fileName);

        CsvImport csvImport = new CsvImport();
        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8

        try {
            Map<String, List<PowerSpotRecord>> powerSpotMap = new HashMap<>();
            
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
                powerSpotMap.computeIfAbsent(mqDefinition, k -> new ArrayList<>())
                        .add(new PowerSpotRecord(dt.sqlDate().toLocalDate(), price));
                cnt++;
            }

            insertOrUpdatePowerSpot(powerSpotMap);
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

    private void insertOrUpdatePowerSpot(Map<String, List<PowerSpotRecord>> powerSpotMap) {

        powerSpotMap.forEach((ticker, powerSpotList) -> {

            PowerSpotPriceDefinition definition = powerSpotPriceDefinitionDAO.findByCodeWithQuotes(ticker);

            if (definition == null) {
                throw new IllegalArgumentException("Power spot definition not found: " + ticker);
            }

            for (PowerSpotRecord record : powerSpotList) {

                LocalDate deliveryDate = record.deliveryDate();

                Optional<PowerSpotPrice> existing
                        = definition.getPrices().stream()
                                .filter(p -> deliveryDate.equals(
                                p.getDeliveryDate()))
                                .findFirst();

                if (existing.isPresent()) {

                    existing.get().setPrice(
                            BigDecimal.valueOf(record.price()));

                } else {

                    PowerSpotPrice newPrice = new PowerSpotPrice();
                    newPrice.setDeliveryDate(deliveryDate);
                    newPrice.setPrice(
                            BigDecimal.valueOf(record.price()));

                    definition.getPrices().add(newPrice);
                }
            }

            powerSpotPriceDefinitionDAO.saveOrUpdate(definition);
        });
    }

    private record PowerSpotRecord(LocalDate deliveryDate, double price) {

    }
}
