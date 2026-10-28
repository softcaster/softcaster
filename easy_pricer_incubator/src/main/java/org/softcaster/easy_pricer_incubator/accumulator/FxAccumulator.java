/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.accumulator;

public class FxAccumulator {

    double strike;
    double knockOutBarrier;
    double leverageFactor; // es. 2.0 per Double Accumulator, 1.0 standard
    int observationDays;
    double dailyNotional;  // Quantità base accumulata ogni giorno

    public FxAccumulator(double strike, double knockOutBarrier, double leverageFactor, int observationDays, double dailyNotional) {
        this.strike = strike;
        this.knockOutBarrier = knockOutBarrier;
        this.leverageFactor = leverageFactor;
        this.observationDays = observationDays;
        this.dailyNotional = dailyNotional;
    }
}
