package mds.core.test;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.AbstractVolatilityForward;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.calc.VolatilityProvider;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.market_data.services.Eurext.EurexApiClient;
import org.softcaster.market_data.services.Eurext.FutureContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Forward per scadenza di SX5E-VOL (dopo TestIndexVolatility): confronto con
 * i settlement FESX delle stesse scadenze e pricing del warrant BNP
 * NLBNPIT3V7A9 con il forward rilevato. Da lanciare su una copia del database.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.softcaster.core.data",
    "org.softcaster.engine",
    "org.softcaster.easy_pricer_mds_core"
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestIndexForward implements CommandLineRunner {

    @Autowired
    private VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    private VolatilityProvider volatilityProvider;
    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("marketDataService")
    private org.softcaster.easy_pricer_mds_core.MarketDataService marketDataService;

    public static void main(String[] args) {
        SpringApplication.run(TestIndexForward.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves();
        VolatilitySurfaceEntity surface = volatilitySurfaceDAO.findByCode("SX5E-VOL").orElseThrow();
        Integer sx5e = surface.getUnderlying().getIdMasterData();
        List<? extends AbstractVolatilityForward> forwards = volatilitySurfaceDAO.findLatestForwards(surface.getIdVolatilitySurface(),
                LocalDate.of(9999, 12, 31));
        LocalDate date = forwards.get(0).getReferenceDate();
        System.out.println("FWD " + forwards.size() + " forwards as of " + date + ", spot at import " + forwards.get(0).getSpot());

        // forward rilevati contro i settlement FESX delle stesse scadenze
        for (FutureContract f : EurexApiClient.findFutures("FESX")) {
            LocalDate expiry = LocalDate.parse(f.expirationDate());
            if (f.settlementPrice() == null || expiry.isAfter(date.plusYears(2))) {
                continue;
            }
            double fwd = volatilityProvider.forward(sx5e, date, expiry, null).orElse(Double.NaN);
            System.out.printf("FWD %s parity %.2f FESX %.2f (settlement %s) diff %.2f%n", expiry, fwd, f.settlementPrice(),
                    f.settlementDate(), fwd - f.settlementPrice());
        }

        PricingRequest surfaceVol = new PricingRequest();
        surfaceVol.isin = "NLBNPIT3V7A9";
        surfaceVol.referenceDate = date;
        print("surface vol", optionCalculator.optionValuation(surfaceVol));

        PricingRequest implied = new PricingRequest();
        implied.isin = "NLBNPIT3V7A9";
        implied.referenceDate = date;
        implied.optionPrice = 0.3025;
        print("implied from 0.3025", optionCalculator.optionValuation(implied));
        System.exit(0);
    }

    private static void print(String label, PricingResponse r) {
        System.out.printf("PRICE %s: premium/warrant %.6f vol %.4f (%s) forward %.2f df %.6f delta/warrant %.6f%n",
                label, r.optionPremium, r.volatility, r.volatilitySource, r.underlyingForward, r.discountFactor, r.delta);
    }
}
