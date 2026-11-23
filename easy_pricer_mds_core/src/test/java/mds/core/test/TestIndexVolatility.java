package mds.core.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.softcaster.core.data.SpotOptionMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.batches.IndexVolatilityImportHelper;
import org.softcaster.easy_pricer_mds_core.batches.VolatilityImportHelper;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.calc.VolatilityProvider;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.market_data.services.Eurext.EurexApiClient;
import org.softcaster.market_data.services.Eurext.OptionSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Superficie SX5E-VOL dai settlement Eurex OESX e lettura di un punto.
 * Da lanciare su una copia del database (spring.datasource.url).
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.softcaster.core.data",
    "org.softcaster.engine",
    "org.softcaster.easy_pricer_mds_core"
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestIndexVolatility implements CommandLineRunner {

    @Autowired
    private IndexVolatilityImportHelper indexVolatilityImportHelper;
    @Autowired
    private VolatilityProvider volatilityProvider;
    @Autowired
    private org.softcaster.core.data.IndexMasterDataDAO indexMasterDataDAO;

    public static void main(String[] args) {
        SpringApplication.run(TestIndexVolatility.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDate today = LocalDate.now();
        List<OptionSeries> series = EurexApiClient.findSeriesBetween("OESX", today, today.plusYears(2));
        List<IndexVolatilityImportHelper.OptionSettlement> settlements = new ArrayList<>();
        for (OptionSeries s : series) {
            if (s.versionNr() != 0 || s.settlementPrice() == null || s.settlementDate() == null || s.settlementDate().isBlank()) {
                continue;
            }
            settlements.add(new IndexVolatilityImportHelper.OptionSettlement(LocalDate.parse(s.settlementDate()),
                    LocalDate.parse(s.expirationDate()), s.strike(),
                    "C".equals(s.callPut()) ? OptionType.CALL : OptionType.PUT, s.settlementPrice()));
        }
        System.out.println("Eurex series: " + series.size() + ", with settlement: " + settlements.size());
        VolatilityImportHelper.ImportResult r = indexVolatilityImportHelper.importSettlements("SX5E-VOL", settlements, 0.5, 0.6, 1.4);
        System.out.println("RESULT " + r.describe());

        Integer sx5e = indexMasterDataDAO.findByCode("SX5E").getIdMasterData();
        Optional<VolatilityProvider.SurfaceData> surface = volatilityProvider.surface(sx5e, r.referenceDate());
        surface.ifPresent(s -> {
            for (double k : new double[]{5000, 5600, 5800, 6000, 6200, 6400}) {
                System.out.printf("VOL 2026-12-18 K=%.0f -> %.4f%n", k, s.surface().volatility(LocalDate.of(2026, 12, 18), k));
            }
            System.out.printf("VOL 2027-06-18 K=6000 -> %.4f%n", s.surface().volatility(LocalDate.of(2027, 6, 18), 6000));
        });
        System.exit(0);
    }
}
