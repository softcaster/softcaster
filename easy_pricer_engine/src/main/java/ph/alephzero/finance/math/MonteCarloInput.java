/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.math;

import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author softc
 */
public class MonteCarloInput {

    public double initialRate = 0.;    // Tasso iniziale (3.5%)
    public double meanReversion = 0.;    // Velocità di ritorno alla media (a)
    public double longTermTarget = 0.;  // Target di lungo periodo (b)
    public double volatility = 0.;      // Volatilità annua (sigma)
    public int daysToMaturity = 0;       // Orizzonte temporale in giorni
    public int numSimulations = 0;    // Numero di iterazioni
    public DayCountBasis daycount;

}
