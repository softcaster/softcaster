package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import org.softcaster.core.data.FloatingRateTerms;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.engine.enums.FixingDayType;

/**
 * Regole del fixing di un tasso variabile (titoli e mutui, FloatingRateTerms):
 * l'indice si fissa fixingLagDays prima dell'inizio del periodo, giorni
 * lavorativi del calendario dello strumento o di calendario; la cedola / rata
 * e' indice + spread, limitato da floor e cap. Tassi in decimale.
 */
public final class RateFixing {

    private RateFixing() {
    }

    public static LocalDate fixingDate(LocalDate accrualStart, FloatingRateTerms terms, Calendar calendar) {
        int lag = terms.getFixingLagDays() == null ? 0 : terms.getFixingLagDays();
        if (terms.getFixingDayType() == FixingDayType.CALENDAR_DAYS) {
            return accrualStart.minusDays(lag);
        }
        LocalDate date = accrualStart;
        for (int i = 0; i < lag; i++) {
            date = date.minusDays(1);
            while (!calendar.isBusinessDay(date)) {
                date = date.minusDays(1);
            }
        }
        return date;
    }

    /**
     * Tasso del periodo dal fixing dell'indice.
     */
    public static double couponRate(double indexFixing, FloatingRateTerms terms) {
        double rate = indexFixing + (terms.getSpread() == null ? 0. : terms.getSpread());
        if (terms.getFloor() != null) {
            rate = Math.max(rate, terms.getFloor());
        }
        if (terms.getCap() != null) {
            rate = Math.min(rate, terms.getCap());
        }
        return rate;
    }
}
