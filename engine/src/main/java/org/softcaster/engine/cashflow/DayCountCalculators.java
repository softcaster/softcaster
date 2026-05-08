/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.temporal.ChronoUnit;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author ep
 */
public class DayCountCalculators {

    public static final DayCountCalculator ACT_360 = (start, end, freq)
            -> ChronoUnit.DAYS.between(start, end) / 360.0;

    public static final DayCountCalculator ACT_365 = (start, end, freq)
            -> ChronoUnit.DAYS.between(start, end) / 365.0;

    public static final DayCountCalculator NASD_30_360 = (start, end, freq) -> {
        int d1 = Math.min(start.getDayOfMonth(), 30);
        int d2 = (d1 == 30) ? Math.min(end.getDayOfMonth(), 30) : end.getDayOfMonth();
        return ((end.getYear() - start.getYear()) * 360
                + (end.getMonthValue() - start.getMonthValue()) * 30
                + (d2 - d1)) / 360.0;
    };
    
    public static final DayCountCalculator ACT_ACT_ICMA = (start, end, freq) -> {
        if (freq == null || freq == Frequency.NONE) {
            // Fallback su ISDA se non abbiamo la frequenza
            return ACT_365.calculate(start, end, freq);
        }

        // Calcoliamo i giorni effettivi tra le date
        double actualDays = ChronoUnit.DAYS.between(start, end);
        
        // Calcoliamo i giorni teorici del periodo basandoci sulla frequenza
        // Es: per un semestrale, la durata teorica dell'anno è (giorni del semestre * 2)
        double daysInYear = actualDays * freq.getYearFraction();
        
        // Se il periodo è regolare (es. esattamente 6 mesi), 
        // il risultato sarà matematicamente 1 / frequenza.
        // Esempio: 182.5 / (182.5 * 2) = 0.5
        return actualDays / daysInYear; 
        
        // Nota: Questa è una versione semplificata. La versione ufficiale ICMA 
        // prevede il calcolo sui giorni del "periodo di riferimento".
    };
}
