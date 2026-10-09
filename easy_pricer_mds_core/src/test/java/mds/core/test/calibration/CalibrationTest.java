/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test.calibration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.softcaster.easy_pricer_mds_core.curve.InstrumentMktData;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.curve.ZSpreadImporter;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.euronext.BorsaItalianaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class CalibrationTest implements CommandLineRunner {

    public static final String CSV_PATH = System.getProperty("user.dir") + "/csv";
    private static final String CSV_SEPARATOR = ";";
    private static final Logger log = LoggerFactory.getLogger(CalibrationTest.class);

    @Autowired
    SecurityMasterDataDAO smdDAO;
    @Autowired
    ZSpreadImporter importer;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;
    @Autowired
    private BondCalculator bondCalculator;

    private record PricingResult(String code, LocalDate maturity, double coupon, double mktPrice, double thPrice) {

    }

    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot 
        SpringApplication.run(CalibrationTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        startCalibrationTest();
    }

    private void startCalibrationTest() {
        long start = System.nanoTime();

        log.info("===  Starting calibration ... ===\n");
        BorsaItalianaProvider provider = BorsaItalianaProvider.getInstance();

        log.info("===  Reading Market Prices ... ===\n");
        List<InstrumentMktData> list = getInstrumentMktDataList(provider);

        log.info("===  Saving ZSpreads ... ===\n");
        saveZspreads(list);

        log.info("===  Pricing Instruments ... ===\n");
        List<PricingResult> resultList = priceIntruments(list);

        log.info("===  Writing csv ... ===\n");
        writeCsv(resultList);

        long end = System.nanoTime();
        long elapsedTime = end - start;
        double milliseconds = elapsedTime / 1_000_000.0;

        log.info("===  Elapsed Time {} milliseconds ===\n", (long) milliseconds);
    }

    private List<InstrumentMktData> getInstrumentMktDataList(AbstractProvider provider) {
        List<InstrumentMktData> list = new ArrayList<>();

        List<SecurityMasterData> dataList = smdDAO.findAllByAssetClass("XRB");

        for (MasterData data : dataList) {
            if(!isPlainFixedBtp(data.getDescription(),data.getInterestRate())) {
                continue;
            }
            Node node = provider.getMktQuote(data.getCode(), Market.BONDS);
            double cleanPrice = node != null ? node.getData().bid() : 0.;
            if (!NumberUtils.isZero(cleanPrice)) {
                list.add(new InstrumentMktData(data.getCode(), cleanPrice));
            }
        }
        return list;
    }

    private boolean saveZspreads(List<InstrumentMktData> list) {
        importer.importZSpread("ITA_SPREADED", list);
        return true;
    }

    private List<PricingResult> priceIntruments(List<InstrumentMktData> list) {
        List<PricingResult> resultList = new ArrayList<>();
        // Riallineo curva
        List<String> names = new ArrayList<>();
        names.add("ITA_SPREADED");
        marketDataService.reloadCurves(names);
        DiscountCurve discountCurve = marketDataService.getDiscountCurve("ITA_SPREADED");
        if (discountCurve != null) {
            PricingResult result;
            for (InstrumentMktData instrument : list) {
                SecurityMasterData smd = smdDAO.findByIsin(instrument.code()).orElse(null);
                if (smd != null) {
                    double price = bondCalculator.calculatePrice(smd, marketDataService.getOfficialDate(), discountCurve);
                    result = new PricingResult(smd.getCode(), smd.getMaturityDate().toLocalDate(), smd.getInterestRate(), instrument.mktPrice(), price);
                    resultList.add(result);
                }
            }
        }

        return resultList;
    }

    private void writeCsv(List<PricingResult> resultList) {
        Path path = Paths.get(CSV_PATH + "/calibration.csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()))) {
            // 1. Scrittura dell'intestazione (Header)
            String[] headers = {
                "Isin",
                "Maturity",
                "Coupon",
                "Mkt Price",
                "Th Price",};
            writer.write(String.join(CSV_SEPARATOR, headers));
            writer.newLine(); // Va a capo

            StringBuilder row;
            for (PricingResult result : resultList) {
                // Costruiamo la riga del CSV                   
                row = new StringBuilder();
                row.append(result.code).append(CSV_SEPARATOR)
                        .append(result.maturity).append(CSV_SEPARATOR)
                        .append(result.coupon).append(CSV_SEPARATOR)
                        .append(result.mktPrice).append(CSV_SEPARATOR)
                        .append(result.thPrice);

                writer.write(row.toString());
                writer.newLine(); // Va a capo per il prossimo record

            }
            writer.close();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    static boolean isPlainFixedBtp(String description, double couponRate) {
        String d = description.toUpperCase();
        return !d.contains("FUTURA") && !d.contains("BTP PIU")
                && !d.contains("ITALIA/TV") && !d.contains("EI") /* inflation-linked, adapt */
                && couponRate > 0.0;
    }
}
