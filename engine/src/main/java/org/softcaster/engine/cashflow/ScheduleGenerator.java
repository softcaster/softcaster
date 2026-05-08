/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public interface ScheduleGenerator {

    /**
     * Genera la lista dei periodi di competenza (accrual) e pagamento.
     *
     * @param effectiveDate
     * @param terminationDate
     * @param frequency
     * @param bdc
     * @param daycount
     * @param calendar
     * @return
     */
    List<PaymentPeriod> generate(LocalDate effectiveDate, LocalDate terminationDate,
            Frequency frequency, BusinessDayConvention bdc, DaycountBasis daycount,
            HolidayCalendar calendar);
}
