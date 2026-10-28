/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.easy_pricer_incubator;

import org.softcaster.easy_pricer_incubator.accumulator.FxAccumulator;
import org.softcaster.easy_pricer_incubator.accumulator.HestonSlvQePricer;
import org.softcaster.easy_pricer_incubator.accumulator.LocalVolSurface;

/**
 *
 * @author ep
 */
public class Easy_pricer_incubator {

    public static void main(String[] args) {
        // Definizione dei nodi discreti della superficie di mercato
        double[] marketTimes = {0.0, 0.25, 0.50, 1.00}; // Scadenze a 0, 3 mesi, 6 mesi, 1 anno
        double[] marketStrikes = {0.9500, 1.0500, 1.1000, 1.1500, 1.2500}; // Range di tassi spot EUR/USD

        // Matrice di Leverage Function (Valori estratti dal modulo di calibrazione PDE della banca)
        // Riga = Tempo, Colonna = Strike
        double[][] leverageMatrix = {
            {1.02, 1.00, 0.98, 1.01, 1.05}, // t = 0.00
            {1.04, 1.01, 0.99, 1.02, 1.07}, // t = 0.25
            {1.05, 1.02, 1.00, 1.04, 1.09}, // t = 0.50
            {1.06, 1.03, 1.01, 1.05, 1.12} // t = 1.00
        };

        // Creazione dell'istanza della superficie
        LocalVolSurface realSLVSurface = new LocalVolSurface(marketTimes, marketStrikes, leverageMatrix);

        // Parametri Heston e di Mercato
        double s0 = 1.1000;
        double v0 = 0.0121;
        double kappa = 2.0;
        double theta = 0.0144;
        double xi = 0.3;
        double rho = -0.4;
        double rDomestic = 0.035;
        double rForeign = 0.045;

        // Istanziazione del pricer quantitativo QE Parallelo
        HestonSlvQePricer pricer = new HestonSlvQePricer(kappa, theta, xi, rho, rDomestic, rForeign, realSLVSurface);

        // Contratto Accumulator: Strike 1.07, KO 1.14, Leva 2x, 60 giorni osservazione, 10.000 importo giornaliero
        FxAccumulator accumulator = new FxAccumulator(1.0700, 1.1400, 2.0, 60, 100000.0);

        // Esecuzione pricing con 250.000 sentieri Monte Carlo
        int paths = 250000;
        long startTime = System.currentTimeMillis();
        double fairValue = pricer.price(accumulator, s0, v0, paths);
        long endTime = System.currentTimeMillis();

        System.out.printf("Fair Value Calcolato: %.4f EUR\n", fairValue);
        System.out.println("Tempo di elaborazione computazionale su ForkJoinPool: " + (endTime - startTime) + " ms");
    }

}
