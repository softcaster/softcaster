/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mds.core.test.power;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.softcaster.core.data.MarketQuote;
import org.softcaster.core.data.MarketQuoteDefinition;
import org.softcaster.core.data.MarketQuoteDefinitionDAO;
import org.softcaster.engine.enums.ShapeGranularity;
import org.softcaster.engine.shape.ShapeCalculationInput;
import org.softcaster.engine.shape.ShapeCalculationResult;
import org.softcaster.engine.shape.ShapeFactor;
import org.softcaster.engine.shape.ShapeProfileCalculator;
import org.softcaster.engine.shape.SpotObservation;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TestPowerFutures implements CommandLineRunner {

    @Autowired
    private MarketQuoteDefinitionDAO marketQuoteDefinitionDAO;

    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot (NON fare "new DatabaseHelper()")
        SpringApplication.run(TestPowerFutures.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        runPowerFuturesTest();
    }

    private void runPowerFuturesTest() {
        runShapeTest();
    }

    private void runShapeTest() {
        String mqd = "GME-POWER-M";
        MarketQuoteDefinition marketQuoteDefinition = marketQuoteDefinitionDAO.findByCodeWithQuotes(mqd);
        if (marketQuoteDefinition != null) {
            /*
                    .filter(quote -> (quote.getBusinessDate().getMonth() == Month.JANUARY || quote.getBusinessDate().getMonth() == Month.FEBRUARY))
                    .collect(Collectors.toList());
*/
            List<MarketQuote> januaryQuotes = marketQuoteDefinition.getQuotes();

            if (januaryQuotes != null && !januaryQuotes.isEmpty()) {
                List<SpotObservation> observations = new ArrayList<>();
                for (MarketQuote quote : januaryQuotes) {
                    SpotObservation observation = new SpotObservation(quote.getBusinessDate(), quote.getPrice().doubleValue());
                    observations.add(observation);
                }
                LocalDate today = LocalDate.now();
                ShapeCalculationInput input = new ShapeCalculationInput(observations, ShapeGranularity.MONTH_DOW, today, null);
                ShapeProfileCalculator calculator = new ShapeProfileCalculator();
                ShapeCalculationResult result = calculator.calculate(input);
                if (result != null) {
                    List<ShapeFactor> factors = result.factors();
                    // Ordino
                    factors.sort(
                            Comparator.comparing(ShapeFactor::month)
                                    .thenComparing(ShapeFactor::dayOfWeek)
                    );
                    for (ShapeFactor factor : factors) {
                        System.out.println("Month: " + factor.month() + "\t" + "DOW: " + factor.dayOfWeek() + "\t" + "Factor: " + factor.factor());
                    }
                    for (Map.Entry<Integer, Double> entry : result.normalizationChecks().entrySet()) {
                        System.out.println("Normalization Check: " + entry.getKey() + " : " + entry.getValue());
                    }
                }
            }
        }
    }
}
