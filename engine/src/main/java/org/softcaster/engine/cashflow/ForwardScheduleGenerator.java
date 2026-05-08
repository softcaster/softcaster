/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author softc
 */
public class ForwardScheduleGenerator implements ScheduleGenerator {

    @Override
    public List<PaymentPeriod> generate(LocalDate effectiveDate, LocalDate terminationDate, Frequency frequency, BusinessDayConvention bdc, DaycountBasis daycount, HolidayCalendar calendar) {
        List<PaymentPeriod> periods = new ArrayList<>();
        LocalDate currentStart = effectiveDate;
        int monthsToStep = 12 / frequency.getYearFraction();

        // Ciclo in avanti: finché la fine del periodo non raggiunge la scadenza
        while (currentStart.isBefore(terminationDate)) {
            
            // Calcoliamo la fine teorica aggiungendo i mesi
            LocalDate theoreticalEnd = currentStart.plusMonths(monthsToStep);
            
            // Se superiamo la scadenza finale, la tagliamo (gestione stub finale)
            LocalDate actualEnd = theoreticalEnd.isAfter(terminationDate) ? terminationDate : theoreticalEnd;

            // Data di pagamento corretta per i festivi
            LocalDate adjustedPaymentDate = bdc.adjust(actualEnd, calendar);

            // Calcolo della yearFraction (fondamentale per la quota interessi)
            double yearFraction = daycount.calculate(currentStart, actualEnd, frequency);

            periods.add(new PaymentPeriod(
                currentStart, 
                actualEnd, 
                adjustedPaymentDate, 
                yearFraction
            ));

            // Per il prossimo periodo, la fine diventa l'inizio
            currentStart = actualEnd;
        }

        return periods;
    }   
}
