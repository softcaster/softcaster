/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.engine;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.BackwardScheduleGenerator;
import org.softcaster.engine.cashflow.BulletAmortizationStrategy;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.HolidayCalendar;
import org.softcaster.engine.cashflow.PaymentPeriod;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.EnumUtils;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author softc
 */
class DummyCaLendar implements HolidayCalendar {

    @Override
    public boolean isHoliday(LocalDate date) {
        return false;
    }
}
        
public class Engine {

    public static void main(String[] args) {
        
        BackwardScheduleGenerator bsg = new BackwardScheduleGenerator();
        LocalDate effectiveDate = LocalDate.of(1998,11,1);
        LocalDate terminationDate = LocalDate.of(2029,11,1);
        Frequency freq = EnumUtils.fromId(Frequency.class,2);
        BusinessDayConvention bdc = EnumUtils.fromId(BusinessDayConvention.class, 3);
        DummyCaLendar dummy = new DummyCaLendar();
        List<PaymentPeriod> periods = bsg.generate(effectiveDate, terminationDate, freq, bdc, dummy);
        
        BulletAmortizationStrategy bas = new BulletAmortizationStrategy();
        List<CashFlow> cfl = bas.generateCashFlows(100., 0.525, periods, DaycountBasis.ACT_365);
        for(CashFlow cf: cfl) {
            System.out.println(cf.accrualStart() + " - " + cf.accrualEnd() + " - " + cf.interest() + " - " + cf.principal());
        }
    }
}
