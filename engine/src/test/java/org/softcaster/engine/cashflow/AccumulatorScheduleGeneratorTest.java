package org.softcaster.engine.cashflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.softcaster.engine.enums.Frequency;

/**
 * Confronto con il calendario di un sistema di riferimento: accumulator
 * EUR/USD, fixing dal 23/9 al 22/10/2026, regolamento settimanale, spot lag 2.
 */
class AccumulatorScheduleGeneratorTest {

    private static final HolidayCalendar WEEKENDS_ONLY = date -> false;
    private final AccumulatorScheduleGenerator generator = new AccumulatorScheduleGenerator();

    @Test
    void weeklyPeriodsStartFromFirstFixing() {
        List<AccumulatorFixingDate> s = generator.generate(LocalDate.of(2026, 9, 23), LocalDate.of(2026, 10, 22),
                Frequency.WEEKLY, 2, WEEKENDS_ONLY);
        assertEquals(22, s.size());
        // {primo fixing del periodo, esercizio, consegna, numero di fixing}
        Object[][] expected = {
            {"2026-09-23", "2026-09-29", "2026-10-01", 5},
            {"2026-09-30", "2026-10-06", "2026-10-08", 5},
            {"2026-10-07", "2026-10-13", "2026-10-15", 5},
            {"2026-10-14", "2026-10-20", "2026-10-22", 5},
            {"2026-10-21", "2026-10-22", "2026-10-26", 2}};
        int i = 0;
        for (Object[] p : expected) {
            int n = (Integer) p[3];
            assertEquals(LocalDate.parse((String) p[0]), s.get(i).fixingDate());
            for (int j = i; j < i + n; j++) {
                assertEquals(LocalDate.parse((String) p[1]), s.get(j).exerciseDate());
                assertEquals(LocalDate.parse((String) p[2]), s.get(j).settlementDate());
            }
            i += n;
        }
    }

    @Test
    void monthlyPeriodsAreAnniversariesOfFirstFixing() {
        List<LocalDate> settlements = generator.generate(LocalDate.of(2026, 9, 23), LocalDate.of(2026, 12, 15),
                Frequency.MONTHLY, 2, WEEKENDS_ONLY).stream().map(AccumulatorFixingDate::settlementDate).distinct().toList();
        // 23/9-22/10 -> 26/10; 23/10-20/11 (22/11 e' domenica) -> 24/11; 23/11-15/12 -> 17/12
        assertEquals(List.of(LocalDate.of(2026, 10, 26), LocalDate.of(2026, 11, 24), LocalDate.of(2026, 12, 17)), settlements);
    }

    @Test
    void fixingAndSettlementCalendarsAreSeparate() {
        // 12/10 festivo solo USD: e' un fixing (calendario EUR), ma il regolamento salta i festivi di entrambe
        HolidayCalendar eur = date -> false;
        HolidayCalendar eurUsd = date -> date.equals(LocalDate.of(2026, 10, 12)) || date.equals(LocalDate.of(2026, 10, 15));
        List<AccumulatorFixingDate> s = generator.generate(LocalDate.of(2026, 9, 23), LocalDate.of(2026, 10, 22),
                Frequency.WEEKLY, 2, eur, eurUsd);
        assertEquals(22, s.size());
        assertEquals(LocalDate.of(2026, 10, 12), s.get(13).fixingDate());
        // terzo periodo 7/10-13/10: 14/10, (15/10 festivo), 16/10
        assertEquals(LocalDate.of(2026, 10, 16), s.get(14).settlementDate());
    }

    @Test
    void holidaysAreSkippedInFixingsAndSettlement() {
        // festivo il 29/9: il primo periodo finisce il 28/9 e regola due giorni lavorativi dopo (1/10)
        HolidayCalendar calendar = date -> date.equals(LocalDate.of(2026, 9, 29));
        List<AccumulatorFixingDate> s = generator.generate(LocalDate.of(2026, 9, 23), LocalDate.of(2026, 10, 6),
                Frequency.WEEKLY, 2, calendar);
        assertEquals(9, s.size());
        assertEquals(LocalDate.of(2026, 9, 28), s.get(3).exerciseDate());
        assertEquals(LocalDate.of(2026, 10, 1), s.get(3).settlementDate());
    }
}
