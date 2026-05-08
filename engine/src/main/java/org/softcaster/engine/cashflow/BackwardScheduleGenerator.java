/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public class BackwardScheduleGenerator implements ScheduleGenerator {

    @Override
    public List<PaymentPeriod> generate(LocalDate effectiveDate, LocalDate terminationDate,
            Frequency frequency, BusinessDayConvention bdc,DaycountBasis daycount,
            HolidayCalendar calendar) {
        
        // Non puo generare un cash flow
        if(frequency.getYearFraction() <= 0)
            return null;
        
        List<PaymentPeriod> periods = new ArrayList<>();
        LocalDate currentEnd = terminationDate;
        int monthsToSubtract = 12 / frequency.getYearFraction();

        // Generazione all'indietro
        while (currentEnd.isAfter(effectiveDate)) {
            // Calcolo l'inizio teorico del periodo sottraendo i mesi della frequenza
            LocalDate theoreticalStart = currentEnd.minusMonths(monthsToSubtract);

            // Se andando indietro supero la data di emissione, mi fermo a effectiveDate
            // Questo crea automaticamente lo "Short First Coupon"
            LocalDate actualStart = theoreticalStart.isBefore(effectiveDate) ? effectiveDate : theoreticalStart;

            // La data di pagamento effettiva segue le regole dei mercati (festivi)
            LocalDate adjustedPayment = bdc.adjust(currentEnd, calendar);

            // Aggiungiamo il periodo (la frazione d'anno verrà calcolata dalla strategia di pricing)
            periods.add(new PaymentPeriod(actualStart, currentEnd, adjustedPayment, daycount.calculate(actualStart, currentEnd,frequency)));

            // Per il ciclo successivo, la fine del periodo diventa l'inizio di quello appena creato
            currentEnd = actualStart;
        }

        // Invertiamo la lista per averla in ordine cronologico (da emissione a scadenza)
        Collections.reverse(periods);
        return periods;
    }
}
