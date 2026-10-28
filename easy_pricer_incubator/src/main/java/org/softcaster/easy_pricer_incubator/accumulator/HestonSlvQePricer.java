/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.accumulator;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class HestonSlvQePricer {

    private final double kappa;
    private final double theta;
    private final double xi;
    private final double rho;
    private final double rDomestic;
    private final double rForeign;
    private final LeverageFunction leverageFunction;

    public HestonSlvQePricer(double kappa, double theta, double xi, double rho,
            double rDomestic, double rForeign, LeverageFunction leverageFunction) {
        this.kappa = kappa;
        this.theta = theta;
        this.xi = xi;
        this.rho = rho;
        this.rDomestic = rDomestic;
        this.rForeign = rForeign;
        this.leverageFunction = leverageFunction;
    }

    public double price(FxAccumulator contract, double s0, double v0, int numPaths) {
        double dt = 1.0 / 252.0;
        double sqrtDt = Math.sqrt(dt);
        double drift = rDomestic - rForeign;

        // --- Pre-calcolo delle costanti dello schema QE di Andersen ---
        double eMinusKt = Math.exp(-kappa * dt);

        // Costanti per la media (m) e la varianza (s2) condizionate di v
        double c1 = theta * (1.0 - eMinusKt);
        double c2 = (xi * xi * eMinusKt / kappa) * (1.0 - eMinusKt);
        double c3 = (theta * xi * xi / (2.0 * kappa)) * (1.0 - eMinusKt) * (1.0 - eMinusKt);

        // Soglia critica raccomandata da Andersen per discriminare i due regimi
        double psiCcritica = 1.5;

        double totalPayoffSum = IntStream.range(0, numPaths)
                .parallel()
                .mapToDouble(p -> {
                    ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                    double S = s0;
                    double v = v0;
                    double pathPayoff = 0.0;

                    for (int t = 1; t <= contract.observationDays; t++) {
                        double time = t * dt;

                        // ----------------------------------------------------
                        // STEP 1: Aggiornamento della Varianza (v) via Schema QE
                        // ----------------------------------------------------
                        double m = v * eMinusKt + c1;
                        double s2 = v * xi * xi * eMinusKt * (1.0 - eMinusKt) / kappa + c3;
                        double psi = (s2) / (m * m);

                        double vNext;
                        double uV = localRandom.nextDouble(); // Variabile uniforme [0, 1)

                        if (psi <= psiCcritica) {
                            // REGIME A: Variabile Normale al Quadrato (Volatilità medio-alta)
                            double b2 = 2.0 / psi - 1.0 + Math.sqrt(2.0 / psi) * Math.sqrt(2.0 / psi - 1.0);
                            double b = Math.sqrt(b2);
                            double a = m / (1.0 + b2);

                            // Invertiamo la cumulativa normale Gaussiana
                            double zV = inverseCumulativeNormal(uV);
                            vNext = a * (b + zV) * (b + zV);
                        } else {
                            // REGIME B: Distribuzione Esponenziale (Volatilità vicina a zero)
                            double pAnt = (psi - 1.0) / (psi + 1.0);
                            double beta = (1.0 - pAnt) / m;

                            if (uV < pAnt) {
                                vNext = 0.0;
                            } else {
                                vNext = -Math.log((1.0 - uV) / (1.0 - pAnt)) / beta;
                            }
                        }

                        // ----------------------------------------------------
                        // STEP 2: Aggiornamento dello Spot (S) con Leverage Function
                        // ----------------------------------------------------
                        double zS = localRandom.nextGaussian();
                        double L = leverageFunction.evaluate(time, S);

                        // Nello schema QE, lo step dello spot integra la variazione di v 
                        // per mantenere la consistenza della correlazione rho
                        double volS = L * Math.sqrt(Math.max(v, 0.0));

                        // Integrazione della componente stocastica dello Spot condizionata a v
                        double predictor = drift * S * dt
                                + volS * S * sqrtDt * (rho * (vNext - v - kappa * (theta - v) * dt) / xi
                                + Math.sqrt(1.0 - rho * rho) * zS);

                        S = S + predictor;
                        v = vNext; // Assegnazione della nuova varianza per il prossimo step

                        // ----------------------------------------------------
                        // STEP 3: Rilevazione dei Payoff dell'Accumulator
                        // ----------------------------------------------------
                        if (S >= contract.knockOutBarrier) {
                            break; // Barriera colpita
                        }

                        if (S > contract.strike) {
                            pathPayoff += (S - contract.strike) * contract.dailyNotional;
                        } else {
                            pathPayoff += (S - contract.strike) * contract.dailyNotional * contract.leverageFactor;
                        }
                    }
                    return pathPayoff;
                })
                .sum();

        double averagePayoff = totalPayoffSum / numPaths;
        return averagePayoff * Math.exp(-rDomestic * (contract.observationDays * dt));
    }

    /**
     * Approssimazione razionale ad alta precisione di Moro (1995) per l'inversa
     * della funzione di ripartizione Normale Standard.
     */
    private double inverseCumulativeNormal(double u) {
        double x = u - 0.5;
        if (Math.abs(x) < 0.42) {
            double r = x * x;
            return x * (((a4 * r + a3) * r + a2) * r + a1) / ((((b4 * r + b3) * r + b2) * r + b1) * r + 1.0);
        }
        double r = u < 0.5 ? u : 1.0 - u;
        r = Math.log(-Math.log(r));
        double x1 = c1_m + r * (c2_m + r * (c3_m + r * (c4_m + r * (c5_m + r * (c6_m + r * (c7_m + r * (c8_m + r * c9_m)))))));
        return u < 0.5 ? -x1 : x1;
    }

    // Costanti matematiche per l'algoritmo di inversione di Moro
    private static final double a1 = 2.50662823884;
    private static final double a2 = -18.61500062529;
    private static final double a3 = 41.39119773534;
    private static final double a4 = -25.04110746790;
    private static final double b1 = -8.47351093090;
    private static final double b2 = 23.08336743743;
    private static final double b3 = -21.06224101826;
    private static final double b4 = 3.13082909833;
    private static final double c1_m = 0.3374754822726147;
    private static final double c2_m = 0.9761690132232696;
    private static final double c3_m = 0.1607979714918209;
    private static final double c4_m = 0.0276438810333863;
    private static final double c5_m = 0.0038405729373609;
    private static final double c6_m = 0.0003951896511919;
    private static final double c7_m = 0.0000321767881768;
    private static final double c8_m = 0.0000002888167364;
    private static final double c9_m = 0.0000003960315187;
}
