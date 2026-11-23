package mds.core.test;

import java.time.LocalDate;
import org.softcaster.core.data.IndexFutureMasterData;
import org.softcaster.core.data.IndexFutureMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Pricing di una serie OESX (Black-76 sul forward dell'indice) con la
 * volatilita' della superficie SX5E-VOL e implicita dal premio di settlement;
 * lettura di una scadenza FESX con testata e indice. Da lanciare su una copia
 * del database con la serie OESXZ6C6200 e la scadenza FESXZ6.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.softcaster.core.data",
    "org.softcaster.engine",
    "org.softcaster.easy_pricer_mds_core"
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestIndexOption implements CommandLineRunner {

    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    private IndexFutureMasterDataDAO indexFutureMasterDataDAO;
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("marketDataService")
    private org.softcaster.easy_pricer_mds_core.MarketDataService marketDataService;

    public static void main(String[] args) {
        SpringApplication.run(TestIndexOption.class, args);
    }

    @Override
    public void run(String... args) {
        LocalDate date = LocalDate.of(2026, 11, 20);
        // cache delle quotazioni e curve, come all'avvio di srv
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves();

        PricingRequest surface = new PricingRequest();
        surface.isin = "OESXZ6C6200";
        surface.referenceDate = date;
        print("surface vol", optionCalculator.optionValuation(surface));

        PricingRequest implied = new PricingRequest();
        implied.isin = "OESXZ6C6200";
        implied.referenceDate = date;
        implied.optionPrice = 40.8;
        print("implied from 40.8", optionCalculator.optionValuation(implied));

        IndexFutureMasterData fesx = indexFutureMasterDataDAO.findByCode("FESXZ6");
        System.out.println("FUTURE " + fesx.getCode() + " product " + fesx.getProduct().getCode()
                + " underlying id " + fesx.getUnderlying().getIdMasterData() + " contract value " + fesx.getContractValue()
                + " asset class " + fesx.getAssetClass().getCode());
        System.exit(0);
    }

    private static void print(String label, PricingResponse r) {
        System.out.printf("PRICE %s: model %s premium %.4f vol %.4f (%s) forward %.2f df %.6f delta %.4f%n", label,
                r.optionModel, r.optionPremium, r.volatility, r.volatilitySource, r.underlyingForward, r.discountFactor, r.delta);
    }
}
