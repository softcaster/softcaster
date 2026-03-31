/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.rate;

/**
 *
 * @author softc
 */
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ph.alephzero.finance.DayCountBasis;

public class EuriborMonteCarlo {

    public static void main(String[] args) {
        // --- Parametri di Input ---
        double initialRate = 0.02157;    // Tasso iniziale (3.5%)
        double meanReversion = 0.22;    // Velocità di ritorno alla media (a)
        double longTermTarget = 0.0258;  // Target di lungo periodo (b)
        double volatility = 0.0215;      // Volatilità annua (sigma)
        int daysToMaturity = 90;       // Orizzonte temporale in giorni
        int numSimulations = 10000;    // Numero di iterazioni

        // Utilizziamo DescriptiveStatistics per calcolare media, deviazione standard, ecc.
        DescriptiveStatistics stats = new DescriptiveStatistics();

        // Generatore di numeri casuali professionale (Mersenne Twister)
        RandomGenerator rng = new MersenneTwister();

        double dt = 1.0 / 360.0; // Base 360 per Euribor

        System.out.println("Simulazione in corso con Apache Commons Math...");

        // --- Loop delle Simulazioni ---
        for (int i = 0; i < numSimulations; i++) {
            double currentRate = initialRate;

            for (int t = 0; t < daysToMaturity; t++) {
                // Genera un numero casuale da distribuzione normale standard (0, 1)
                double dWt = rng.nextGaussian();

                // Modello di Vasicek
                double dr = meanReversion * (longTermTarget - currentRate) * dt
                        + volatility * Math.sqrt(dt) * dWt;

                currentRate += dr;
                //System.out.println(currentRate);
            }
            // Aggiunge il risultato finale alla statistica
            stats.addValue(currentRate);
        }

        // --- Output dei Risultati ---
        System.out.println("------------------------------------------------");
        System.out.printf("Risultati su %d simulazioni a %d giorni:%n", numSimulations, daysToMaturity);
        System.out.printf("Media Attesa (Expected): %.4f%%%n", stats.getMean() * 100);
        System.out.printf("Deviazione Standard:     %.4f%%%n", stats.getStandardDeviation() * 100);
        System.out.printf("Minimo (Best Case):      %.4f%%%n", stats.getMin() * 100);
        System.out.printf("Massimo (Worst Case):    %.4f%%%n", stats.getMax() * 100);

        // Calcolo del Percentile (es. Value at Risk al 95%)
        System.out.printf("Percentile 95%% (VaR):    %.4f%%%n", stats.getPercentile(95) * 100);
        System.out.println("------------------------------------------------");
    }
}
