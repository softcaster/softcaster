/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test.zspread;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.curve.ZSpreadImporter;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.euronext.BorsaItalianaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class ZSpreadBuilder implements CommandLineRunner {

    public static final String CSV_PATH = System.getProperty("user.dir") + "/csv";

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    private BondCalculator bondCalculator;

    @Autowired
    @Qualifier("marketDataService") // Indica a Spring esattamente QUALE bean usare
    private MarketDataService marketDataService;

    @Autowired
    private ZSpreadImporter importer;

    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot 
        SpringApplication.run(ZSpreadBuilder.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        testDiscountCurve("ECBYC");
    }

    private void testDiscountCurve(String idCurve) {
        importer.importZSpread(idCurve);
    }

    private void exportZSpread(String idCurve) {
        List<SecurityMasterData> bonds = smdDAO.findAllByAssetClass("XRB");
        Path path = Paths.get(CSV_PATH + "/securities.csv");

        CsvExporterNative exporter = new CsvExporterNative();
        exporter.exportMasterDataToCsv(bonds, path.toString());
    }

    private class CsvExporterNative {

        // Definiamo il separatore (il punto e virgola è l'ideale per Excel in italiano)
        private static final String CSV_SEPARATOR = ";";

        public void exportMasterDataToCsv(List<SecurityMasterData> dataList, String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                // 1. Scrittura dell'intestazione (Header)
                String[] headers = {
                    "Code",
                    "Description",
                    "Maturity Date",
                    "Interest Rate",
                    "Clean Price",
                    "Z-Spread"
                };
                writer.write(String.join(CSV_SEPARATOR, headers));
                writer.newLine(); // Va a capo

                BorsaItalianaProvider provider = BorsaItalianaProvider.getInstance();
                // 2. Scrittura dei dati
                for (MasterData data : dataList) {

                    // Estraiamo in sicurezza i campi, gestendo i potenziali NullPointerException
                    String code = sanitize(data.getCode());
                    Node node = provider.getMktQuote(code, Market.BONDS);
                    double cleanPrice = node != null ? node.getData().bid() : 0.;
                    if (NumberUtils.isZero(cleanPrice)) {
                        continue;
                    }
                    String description = sanitize(data.getDescription());
                    if (!isPlainFixedBtp(description, data.getInterestRate())) {
                        continue;
                    }

                    System.out.println("Elaborating instrument: " + code);

                    // Gestione di date e numeri
                    String maturityDate = (data.getIssueDate() != null) ? data.getMaturityDate().toString() : "";
                    String interestRate = (data.getInterestRate() != null) ? String.valueOf(data.getInterestRate()) : "";

                    // Costruiamo la riga del CSV                   
                    double zSpread = calcZSpread(cleanPrice, data);
                    StringBuilder row = new StringBuilder();
                    row.append(code).append(CSV_SEPARATOR)
                            .append(description).append(CSV_SEPARATOR)
                            .append(maturityDate).append(CSV_SEPARATOR)
                            .append(interestRate).append(CSV_SEPARATOR)
                            .append(cleanPrice).append(CSV_SEPARATOR)
                            .append(zSpread);

                    writer.write(row.toString());
                    writer.newLine(); // Va a capo per il prossimo record
                }

            } catch (IOException e) {
                LoggerMgr.logError(e.getLocalizedMessage());
            }
        }

        /**
         * Pulisce le stringhe per evitare che caratteri speciali (come i punti
         * e virgola interni o i ritorni a capo) rompano la struttura del file
         * CSV.
         */
        private static String sanitize(String value) {
            if (value == null) {
                return "";
            }
            // Se la stringa contiene il separatore o le virgolette, la racchiudiamo tra virgolette
            if (value.contains(CSV_SEPARATOR) || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
                // Raddoppia le virgolette esistenti per fare l'escaping secondo lo standard CSV
                value = value.replace("\"", "\"\"");
                return "\"" + value + "\"";
            }
            return value;
        }

        private double calcZSpread(double cleanPrice, MasterData data) {
            double zSpread = 0.;
            if (data instanceof SecurityMasterData) {
                SecurityMasterData smd = smdDAO.findByCodeWithCashFlowAndHolidays(data.getCode());
                if (smd.getCashFlows().isEmpty()) {
                    System.out.println("Instrument: " + smd.getCode() + " has not cashflow!");
                    return zSpread;
                }
                marketDataService.loadCurveCurveRates("ECBYC");
                org.softcaster.engine.curve.YieldCurve yieldCurve = marketDataService.getYieldCurve("ECBYC");
                if (yieldCurve != null) {
                    Calendar calendar = new Calendar(smd.getCurrency());
                    LocalDate valuationDate = calendar.getNextBusinessDate(marketDataService.getOfficialDate(), smd.getBusinessDays());
                    double accrual = bondCalculator.getAccruals(smd, valuationDate);
                    double dirtyPrice = cleanPrice + accrual;
                    zSpread = bondCalculator.getZSpread(smd, dirtyPrice, valuationDate, yieldCurve);
                }
            }
            return zSpread;
        }

        static boolean isPlainFixedBtp(String description, double couponRate) {
            String d = description.toUpperCase();
            return !d.contains("FUTURA") && !d.contains("BTP PIU")
                    && !d.contains("ITALIA/TV") && !d.contains("EI") /* inflation-linked, adapt */
                    && couponRate > 0.0;
        }
    }
}
