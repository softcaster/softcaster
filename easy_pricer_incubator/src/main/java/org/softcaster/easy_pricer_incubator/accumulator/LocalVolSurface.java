/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.accumulator;

/**
 * Implementazione industriale thread-safe di una superficie di Volatilità
 * Locale 2D basata su una griglia discreta con interpolazione bilineare.
 */
public class LocalVolSurface implements LeverageFunction {

    private final double[] times;     // Nodi temporali (ordinate X, es. 0.1, 0.25, 0.5, 1.0)
    private final double[] strikes;   // Nodi di prezzo/strike (ascisse Y, es. 0.9, 1.0, 1.1, 1.2)
    private final double[][] data;    // Matrice dei valori di Leverage Function L(t, S) [timeIndex][strikeIndex]

    public LocalVolSurface(double[] times, double[] strikes, double[][] data) {
        // Validazione minima delle dimensioni
        if (data.length != times.length || data[0].length != strikes.length) {
            throw new IllegalArgumentException("Le dimensioni della matrice non corrispondono ai vettori dei nodi.");
        }
        this.times = times;
        this.strikes = strikes;
        this.data = data;
    }

    /**
     * Valuta L(t, S) tramite interpolazione bilineare sulla griglia 2D. Metodo
     * nativamente thread-safe poiché legge solo strutture dati immutabili.
     * @param t
     * @param S
     * @return 
     */
    @Override
    public double evaluate(double t, double S) {
        // 1. Gestione del confinamento temporale (Extrapolation sul tempo)
        int tIdx = findLowerIndex(times, t);
        if (tIdx < 0) {
            tIdx = 0;
        }
        if (tIdx >= times.length - 1) {
            tIdx = times.length - 2;
        }

        // 2. Gestione del confinamento dello Strike (Extrapolation sul prezzo)
        int sIdx = findLowerIndex(strikes, S);
        if (sIdx < 0) {
            sIdx = 0;
        }
        if (sIdx >= strikes.length - 1) {
            sIdx = strikes.length - 2;
        }

        // Recupero dei 4 nodi adiacenti della griglia
        double t0 = times[tIdx];
        double t1 = times[tIdx + 1];
        double s0 = strikes[sIdx];
        double s1 = strikes[sIdx + 1];

        double q11 = data[tIdx][sIdx];         // Punto in basso a sinistra
        double q12 = data[tIdx][sIdx + 1];     // Punto in alto a sinistra
        double q21 = data[tIdx + 1][sIdx];     // Punto in basso a destra
        double q22 = data[tIdx + 1][sIdx + 1]; // Punto in alto a destra

        // 3. Formule matematiche dell'interpolazione bilineare
        double tDiff = t1 - t0;
        double sDiff = s1 - s0;

        double w_t1 = (t - t0) / tDiff;
        double w_t0 = 1.0 - w_t1;

        double w_s1 = (S - s0) / sDiff;
        double w_s0 = 1.0 - w_s1;

        // Combinazione pesata lineare dei 4 quadranti
        return w_t0 * w_s0 * q11
                + w_t0 * w_s1 * q12
                + w_t1 * w_s0 * q21
                + w_t1 * w_s1 * q22;
    }

    /**
     * Algoritmo di ricerca binaria ad alta velocità per trovare l'indice del
     * nodo immediatamente inferiore al valore cercato.
     */
    private int findLowerIndex(double[] array, double value) {
        if (value < array[0]) {
            return -1;
        }
        if (value >= array[array.length - 1]) {
            return array.length - 2;
        }

        int low = 0;
        int high = array.length - 1;

        while (high - low > 1) {
            int mid = (low + high) >>> 1; // Bitwise shift per evitare overflow
            if (array[mid] <= value) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return low;
    }
}
