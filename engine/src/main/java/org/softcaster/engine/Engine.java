/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.engine;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.analytics.BondCalculator;
import org.softcaster.engine.cashflow.BackwardScheduleGenerator;
import org.softcaster.engine.cashflow.BulletAmortizationStrategy;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.ForwardScheduleGenerator;
import org.softcaster.engine.cashflow.FrenchAmortizationStrategy;
import org.softcaster.engine.cashflow.HolidayCalendar;
import org.softcaster.engine.cashflow.PaymentPeriod;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.Compounding;
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

    private static void testBondCalculator() {
        BackwardScheduleGenerator bsg = new BackwardScheduleGenerator();
        LocalDate effectiveDate = LocalDate.of(1999, 11, 1);
        LocalDate terminationDate = LocalDate.of(2031, 5, 1);
        Frequency freq = EnumUtils.fromId(Frequency.class, 2);

        BusinessDayConvention bdc = EnumUtils.fromId(BusinessDayConvention.class, 3);
        DaycountBasis daycount = EnumUtils.fromId(DaycountBasis.class, 5);

        DummyCaLendar dummy = new DummyCaLendar();
        List<PaymentPeriod> periods = bsg.generate(effectiveDate, terminationDate, freq, bdc, daycount, dummy);
        BulletAmortizationStrategy bas = new BulletAmortizationStrategy();
        List<CashFlow> flows = bas.generateCashFlows(100., 0.06, periods, DaycountBasis.ACT_ACT_ICMA);

        LocalDate valuationDate = LocalDate.of(2026, 5, 11);
        BondCalculator calculator = new BondCalculator();
        double irr = calculator.calculateYtm(flows, 113.39, valuationDate, DaycountBasis.ACT_365, Compounding.COMPOUNDED, freq);
        System.out.println(irr);
        /*
        for (CashFlow cf : flows) {
            System.out.println(cf.accrualStart() + "," + cf.accrualEnd() + "," + cf.interest() + "," + cf.principal());
        }
         */
        double accruedInterest = calculator.calculateAccruedInterest(flows, valuationDate, DaycountBasis.ACT_365, freq);
        System.out.println(accruedInterest);

        double modifiedDuration = calculator.calculateModifiedDuration(flows, irr, valuationDate, daycount, freq);
        System.out.println(modifiedDuration);

    }

    private static void testFrenchAmortizationStrategy() {
        LocalDate effectiveDate = LocalDate.of(2026, 5, 8);
        LocalDate terminationDate = LocalDate.of(2027, 5, 8);
        Frequency freq = Frequency.MONTHLY;
        BusinessDayConvention bdc = BusinessDayConvention.FORWARD;
        DaycountBasis daycount = DaycountBasis.ACT_365;
        DummyCaLendar dummy = new DummyCaLendar();

        ForwardScheduleGenerator fsg = new ForwardScheduleGenerator();
        List<PaymentPeriod> periods = fsg.generate(effectiveDate, terminationDate, freq, bdc, daycount, dummy);

        FrenchAmortizationStrategy fas = new FrenchAmortizationStrategy();
        List<CashFlow> flows = fas.generateCashFlows(10000., 0.06, periods, DaycountBasis.ACT_ACT_ICMA);

        for (CashFlow cf : flows) {
            System.out.println(cf.accrualStart() + "," + cf.accrualEnd() + "," + cf.interest() + "," + cf.principal());
        }
    }

    public static void main(String[] args) {
        Engine.testFrenchAmortizationStrategy();
    }
}
