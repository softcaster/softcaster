package mds.core.test;

import java.time.LocalDate;
import org.softcaster.core.data.CoveredWarrantProduct;
import org.softcaster.core.data.OptionProductDAO;
import org.softcaster.core.data.SpotOptionMasterData;
import org.softcaster.core.data.SpotOptionMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.engine.dto.OptionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Covered warrant su SX5E (ratio 0,01): testata con emittente e ratio,
 * prezzo per warrant con la superficie SX5E-VOL e volatilita' implicita dal
 * prezzo del warrant. Da lanciare su una copia del database con la testata
 * UCGCW-SX5E e la serie CWSX5EC6200Z6, dopo TestIndexVolatility.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.softcaster.core.data",
    "org.softcaster.engine",
    "org.softcaster.easy_pricer_mds_core"
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class TestCoveredWarrant implements CommandLineRunner {

    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    private OptionProductDAO optionProductDAO;
    @Autowired
    private SpotOptionMasterDataDAO spotOptionMasterDataDAO;
    @Autowired
    private org.softcaster.core.data.VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("marketDataService")
    private org.softcaster.easy_pricer_mds_core.MarketDataService marketDataService;

    public static void main(String[] args) {
        SpringApplication.run(TestCoveredWarrant.class, args);
    }

    @Override
    public void run(String... args) {
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves();
        // data della superficie SX5E-VOL (puo' essere dopo la data ufficiale della copia)
        org.softcaster.core.data.VolatilitySurfaceEntity vs = volatilitySurfaceDAO.findByCode("SX5E-VOL").orElseThrow();
        LocalDate date = volatilitySurfaceDAO.findLastReferenceDate(vs.getIdVolatilitySurface(), LocalDate.of(9999, 12, 31))
                .orElseThrow();
        System.out.println("DATE " + date);

        CoveredWarrantProduct product = optionProductDAO.findByCode("UCGCW-SX5E", CoveredWarrantProduct.class).orElseThrow();
        System.out.println("PRODUCT " + product.getCode() + " issuer " + product.getIssuer().getShortIssuerName()
                + " ratio " + product.getRatio() + " underlying " + product.getUnderlying().getCode()
                + " asset class " + product.getAssetClass().getCode());

        SpotOptionMasterData series = spotOptionMasterDataDAO.getByCodeOrIsin("CWSX5EC6200Z6");
        System.out.println("SERIES " + series.getCode() + " price ratio " + series.getPriceRatio()
                + " contract value " + series.getContractValue());

        PricingRequest surface = new PricingRequest();
        surface.isin = "CWSX5EC6200Z6";
        surface.referenceDate = date;
        PricingResponse rs = optionCalculator.optionValuation(surface);
        print("surface vol", rs);

        PricingRequest implied = new PricingRequest();
        implied.isin = "CWSX5EC6200Z6";
        implied.referenceDate = date;
        implied.optionPrice = 0.40;
        print("implied from 0.40 EUR/warrant", optionCalculator.optionValuation(implied));

        OptionInput input = optionCalculator.getOptionInput("CWSX5EC6200Z6", date, null, null, null, null, rs.volatility);
        System.out.printf("QUOTED price per warrant %.6f = premium per index point %.4f x %.2f%n",
                optionCalculator.quotedPrice(series, input), optionCalculator.valuation(input).price(), series.getPriceRatio());
        System.exit(0);
    }

    private static void print(String label, PricingResponse r) {
        System.out.printf("PRICE %s: premium/warrant %.6f vol %.4f (%s) forward %.2f delta/warrant %.6f vega/warrant %.6f%n",
                label, r.optionPremium, r.volatility, r.volatilitySource, r.underlyingForward, r.delta, r.vega);
    }
}
