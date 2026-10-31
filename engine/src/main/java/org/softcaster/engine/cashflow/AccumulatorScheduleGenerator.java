package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.enums.Frequency;

/**
 * Calendario dei fixing di un accumulator / decumulator: tutti i giorni
 * lavorativi tra il primo e l'ultimo fixing, raggruppati in periodi di
 * regolamento.
 *
 * I periodi partono dal primo fixing, non dal calendario: WEEKLY con primo
 * fixing mercoledi' 23/9 -> 23/9-29/9, 30/9-6/10, ...; MONTHLY (QUARTERLY...)
 * per anniversari mensili del primo fixing; DAILY ogni fixing; NONE tutto il
 * contratto. L'ultimo periodo puo' essere piu' corto. Data di esercizio =
 * ultimo fixing del periodo; regolamento = esercizio + spot lag giorni
 * lavorativi.
 *
 * Due calendari: i fixing seguono quello della fonte di fixing (per i cambi
 * la valuta del fixing, es. EUR per EUR/USD: il 12/10 Columbus Day e' un
 * fixing), il regolamento deve cadere in un giorno lavorativo di entrambe le
 * valute.
 */
public class AccumulatorScheduleGenerator {

    /**
     * Stesso calendario per fixing e regolamento.
     */
    public List<AccumulatorFixingDate> generate(LocalDate firstFixing, LocalDate lastFixing, Frequency frequency,
            int spotLag, HolidayCalendar calendar) {
        return generate(firstFixing, lastFixing, frequency, spotLag, calendar, calendar);
    }

    public List<AccumulatorFixingDate> generate(LocalDate firstFixing, LocalDate lastFixing, Frequency frequency,
            int spotLag, HolidayCalendar fixingCalendar, HolidayCalendar settlementCalendar) {
        Objects.requireNonNull(firstFixing, "firstFixing");
        Objects.requireNonNull(lastFixing, "lastFixing");
        Objects.requireNonNull(frequency, "frequency");
        Objects.requireNonNull(fixingCalendar, "fixingCalendar");
        Objects.requireNonNull(settlementCalendar, "settlementCalendar");
        if (lastFixing.isBefore(firstFixing)) {
            throw new IllegalArgumentException("Last fixing before the first one");
        }
        if (frequency == Frequency.CUSTOM) {
            throw new IllegalArgumentException("Settlement frequency CUSTOM not supported");
        }
        if (spotLag < 0) {
            throw new IllegalArgumentException("Negative spot lag");
        }

        List<LocalDate> dates = new ArrayList<>();
        for (LocalDate d = firstFixing; !d.isAfter(lastFixing); d = d.plusDays(1)) {
            if (fixingCalendar.isBusinessDay(d)) {
                dates.add(d);
            }
        }
        if (dates.isEmpty()) {
            throw new IllegalArgumentException("No business day between " + firstFixing + " and " + lastFixing);
        }

        List<AccumulatorFixingDate> schedule = new ArrayList<>();
        int periodStart = 0;
        for (int i = 0; i < dates.size(); i++) {
            boolean lastOfPeriod = i == dates.size() - 1
                    || period(dates.get(i + 1), firstFixing, frequency) != period(dates.get(i), firstFixing, frequency);
            if (lastOfPeriod) {
                LocalDate exercise = dates.get(i);
                LocalDate settlement = addBusinessDays(exercise, spotLag, settlementCalendar);
                for (int j = periodStart; j <= i; j++) {
                    schedule.add(new AccumulatorFixingDate(dates.get(j), exercise, settlement));
                }
                periodStart = i + 1;
            }
        }
        return schedule;
    }

    /**
     * Indice del periodo di regolamento di una data, contato dal primo fixing.
     */
    private static long period(LocalDate date, LocalDate first, Frequency frequency) {
        return switch (frequency) {
            case DAILY -> date.toEpochDay();
            case WEEKLY -> ChronoUnit.DAYS.between(first, date) / 7;
            case NONE -> 0L;
            default -> ChronoUnit.MONTHS.between(first, date) / (12 / frequency.getYearFraction());
        };
    }

    private static LocalDate addBusinessDays(LocalDate date, int days, HolidayCalendar calendar) {
        LocalDate d = date;
        for (int i = 0; i < days; i++) {
            d = d.plusDays(1);
            while (!calendar.isBusinessDay(d)) {
                d = d.plusDays(1);
            }
        }
        return d;
    }
}
