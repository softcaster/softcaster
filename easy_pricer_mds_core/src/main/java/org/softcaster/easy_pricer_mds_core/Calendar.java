/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import org.softcaster.core.data.Currency;
import org.softcaster.core.data.Holiday;
import org.softcaster.engine.cashflow.HolidayCalendar;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import java.util.stream.Collectors;

public class Calendar implements HolidayCalendar {

    // Usiamo Set anziché List: previene matematicamente i duplicati
    private final Set<Holiday> holidays;

    // Costruttore che accetta una lista di valute
    public Calendar(List<Currency> currencies) {
        if (currencies == null || currencies.isEmpty()) {
            throw new IllegalArgumentException("Currency list cannot be null or empty");
        }

        // Raggruppa tutte le festività eliminando i duplicati basandosi su giorno e mese
        this.holidays = currencies.stream()
                .filter(currency -> currency.getCalendar() != null)
                .flatMap(currency -> currency.getCalendar().getHolidays().stream())
                .filter(distinctByDayAndMonth(h -> h.getHolidayDay() + "-" + h.getHolidayMonth()))
                .collect(Collectors.toSet());
    }

    // Costruttore alternativo "comodo" (Varargs) per passargli le valute separate da virgola
    // Esempio: new Calendar(eur, usd, gbp);
    public Calendar(Currency... currencies) {
        this(List.of(currencies));
    }

    @Override
    public boolean isHoliday(LocalDate date) {
        if (date == null) {
            return false;
        }

        // Per evitare che il confronto fallisca tra anni diversi,
        // dobbiamo verificare anche l'anno se l'entità Holiday supporta l'anno (festività mobili),
        // oppure gestire il match corretto.
        return holidays.stream().anyMatch(holiday
                -> holiday.getHolidayDay() == date.getDayOfMonth()
                && holiday.getHolidayMonth() == date.getMonthValue()
        );
    }

    // Verifica se è un giorno lavorativo (Sabato, Domenica e Festività sono ESCLUSI)
    @Override
    public boolean isBusinessDay(LocalDate date) {
        if (date == null) {
            return false;
        }
        int dayOfWeek = date.getDayOfWeek().getValue();
        // 6 = Sabato, 7 = Domenica
        if (dayOfWeek == 6 || dayOfWeek == 7) {
            return false;
        }
        return !isHoliday(date);
    }

    public LocalDate getNextBusinessDate(java.sql.Date date, int businessDays) {
        return getNextBusinessDate(date.toLocalDate(), businessDays);
    }

    public LocalDate getNextBusinessDate(LocalDate refDate, int businessDays) {
        for (int offset = 1; offset <= businessDays; offset++) {
            refDate = refDate.plusDays(1);
            while (!isBusinessDay(refDate)) {
                refDate = refDate.plusDays(1);
            }
        }
        return refDate;
    }

    public LocalDate getPreviousBusinessDate(java.sql.Date date, int businessDays) {
        return getPreviousBusinessDate(date.toLocalDate(), businessDays);
    }

    public LocalDate getPreviousBusinessDate(LocalDate refDate, int businessDays) {
        // Aggiunto il ciclo for per supportare il parametro 'businessDays'
        // esattamente come fa il metodo getNextBusinessDate
        for (int offset = 1; offset <= businessDays; offset++) {
            refDate = refDate.plusDays(-1);
            while (!isBusinessDay(refDate)) {
                refDate = refDate.plusDays(-1);
            }
        }
        return refDate;
    }

    // Helper statico per filtrare per chiave logica custom
    private static <T> Predicate<T> distinctByDayAndMonth(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
