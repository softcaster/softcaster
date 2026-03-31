/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.math;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.softcaster.commons.utils.NumberUtils;

/**
 *
 * @author softc
 */
public class MonteCarloSimulation {

    public static MonteCarloOutput runSimulation(MonteCarloInput input) {

        if (input != null && input.daycount != null && !NumberUtils.isZero(input.daycount.getTime())) {

            DescriptiveStatistics stats = new DescriptiveStatistics();
            // Generatore di numeri casuali professionale (Mersenne Twister)
            RandomGenerator rng = new MersenneTwister();
            // Output
            MonteCarloOutput output = new MonteCarloOutput();

            double dt = 1.0 / input.daycount.getTime(); // Base 360 per Euribor

            // --- Loop delle Simulazioni ---
            for (int i = 0; i < input.numSimulations; i++) {
                double currentRate = input.initialRate;

                for (int t = 0; t < input.daysToMaturity; t++) {
                    // Genera un numero casuale da distribuzione normale standard (0, 1)
                    double dWt = rng.nextGaussian();

                    // Modello di Vasicek
                    double dr = input.meanReversion * (input.longTermTarget - currentRate) * dt
                            + input.volatility * Math.sqrt(dt) * dWt;

                    currentRate += dr;
                    //System.out.println(currentRate);
                }
                // Aggiunge il risultato finale alla statistica
                stats.addValue(currentRate);
            }

            // --- Output dei Risultati ---
            output.mean = stats.getMean();
            output.standardDeviation = stats.getStandardDeviation();
            output.max = stats.getMax();
            output.min = stats.getMin();
            output.percentile_95 = stats.getPercentile(95);

            return output;
        } else {
            return null;
        }
    }
}
