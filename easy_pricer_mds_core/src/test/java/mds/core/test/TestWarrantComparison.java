package mds.core.test;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.market_data.services.Eurext.EurexApiClient;
import org.softcaster.market_data.services.Eurext.FutureContract;
import org.softcaster.market_data.services.Eurext.OptionSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Confronto tra il plain warrant BNP NLBNPIT3V7A9 (call 6000 SX5E, 19/03/2027,
 * parita' 1.000) e l'OESX con gli stessi termini (DE000F0N4895): premio OESX e
 * forward (FESX marzo 2027) dall'ultimo settlement Eurex, prezzo del warrant
 * da riga di comando (EUR, default 0,3015 = mid 0,3010 / 0,3020).
 *
 * Argomenti: warrant [premioOESX prezzoFESX [aaaa-mm-gg]]; i prezzi di borsa
 * mancanti si prendono dall'ultimo settlement. Per un confronto pulito i tre
 * prezzi vanno presi nello stesso momento (es. mid bid/ask a schermo).
 *   mvn -o exec:java -Dexec.mainClass=mds.core.test.TestWarrantComparison -Dexec.classpathScope=test
 *       -Dexec.args="0.3015 300.5 6010 2026-11-26"
 *
 * Da lanciare su una copia del database con le due serie e la curva ESTR_OIS.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.softcaster.core.data",
    "org.softcaster.engine",
    "org.softcaster.easy_pricer_mds_core"
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestWarrantComparison implements CommandLineRunner {

    private static final String WARRANT = "NLBNPIT3V7A9";
    private static final String LISTED = "DE000F0N4895";
    private static final LocalDate EXPIRY = LocalDate.of(2027, 3, 19);

    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("marketDataService")
    private org.softcaster.easy_pricer_mds_core.MarketDataService marketDataService;

    public static void main(String[] args) {
        SpringApplication.run(TestWarrantComparison.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        double warrantPrice = args.length > 0 ? Double.parseDouble(args[0]) : 0.3015;
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves();

        // ultimo settlement Eurex dell'OESX equivalente e del FESX della stessa scadenza
        List<OptionSeries> chain = EurexApiClient.findSeries("OESX", EXPIRY, 6000.0, 6000.0, "C");
        OptionSeries oesx = chain.stream().filter(s -> LISTED.equals(s.isin())).findFirst().orElseThrow();
        FutureContract fesx = EurexApiClient.findFutures("FESX").stream()
                .filter(f -> EXPIRY.toString().equals(f.expirationDate())).findFirst().orElseThrow();
        // argomenti opzionali per prezzi dello stesso momento: warrant [OESX FESX [data]]
        double listedPrice = args.length > 1 ? Double.parseDouble(args[1]) : oesx.settlementPrice();
        double futurePrice = args.length > 2 ? Double.parseDouble(args[2]) : fesx.settlementPrice();
        LocalDate date = args.length > 3 ? LocalDate.parse(args[3]) : LocalDate.parse(oesx.settlementDate());
        System.out.printf("EUREX settlement %s: OESX C6000 %s = %.2f, FESX %s = %.2f%n", oesx.settlementDate(), EXPIRY,
                oesx.settlementPrice(), fesx.isin(), fesx.settlementPrice());
        System.out.printf("USED %s: warrant %.4f, OESX %.2f, FESX %.2f%n", date, warrantPrice, listedPrice, futurePrice);

        OptionCalculator.WarrantComparison c = optionCalculator.compareWarrant(WARRANT, warrantPrice, LISTED,
                listedPrice, date, futurePrice);
        System.out.printf("CMP forward %.2f  DF %.6f  T %.4f  ratio %.4f%n", c.forward(), c.discountFactor(), c.timeToExpiry(), c.ratio());
        System.out.printf("CMP warrant %.4f EUR = %.2f pts -> vol %.2f%%%n", c.warrantPrice(), c.warrantInPoints(), c.warrantVol() * 100);
        System.out.printf("CMP OESX    %.2f pts -> vol %.2f%%%n", c.listedPrice(), c.listedVol() * 100);
        System.out.printf("CMP spread  %.2f vol pts; fair warrant at OESX vol %.4f EUR; markup %.4f EUR (%.1f%%)%n",
                c.volSpread() * 100, c.fairWarrantPrice(), c.markup(), c.markupPct() * 100);
        System.exit(0);
    }
}
