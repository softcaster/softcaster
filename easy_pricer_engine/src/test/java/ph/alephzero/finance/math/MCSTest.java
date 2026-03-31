/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.math;

import org.softcaster.commons.utils.Converter;
import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author softc
 */
public class MCSTest {

    public static void main(String[] args) {
        MonteCarloInput input = new MonteCarloInput();
        input.initialRate = 1.14635;    // Tasso iniziale (3.5%)
        input.meanReversion = 0.22;    // Velocità di ritorno alla media (a)
        input.longTermTarget = 1.14635;  // Target di lungo periodo (b)
        input.volatility = 0.07;      // Volatilità annua (sigma)
        input.daysToMaturity = 90;       // Orizzonte temporale in giorni
        input.numSimulations = 10000;    // Numero di iterazioni
        input.daycount = DayCountBasis.ACT_360;

        for (int i = 0; i < 20; i++) {
            MonteCarloOutput output = MonteCarloSimulation.runSimulation(input);
            System.out.println(Converter.fromDouble(output.mean));
        }
    }

}
