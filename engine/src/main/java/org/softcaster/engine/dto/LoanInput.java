package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;

/**
 * Mutuo per LoanPricer: piano completo delle rate (dall'erogazione) e dati
 * di mercato per la valutazione.
 *
 * @param principal        capitale finanziato
 * @param disbursementDate data di erogazione (inizio del TAEG)
 * @param annualRate       tasso contrattuale (TAN) in decimale
 * @param schedule         rate: quota capitale, interessi, residuo
 * @param upfrontFees      spese all'erogazione (riducono il netto erogato)
 * @param installmentFees  spese di ogni rata, nello stesso ordine delle rate
 * @param discountCurve    curva di sconto; null = tasso piatto discountRate
 * @param discountRate     tasso di sconto piatto continuo, se manca la curva
 * @param creditSpread     spread di credito continuo sopra la curva o il tasso
 */
public record LoanInput(
        String instrumentId,
        LocalDate valuationDate,
        double principal,
        LocalDate disbursementDate,
        double annualRate,
        CashFlowSchedule schedule,
        double upfrontFees,
        List<Double> installmentFees,
        DiscountCurve discountCurve,
        double discountRate,
        double creditSpread) implements CashFlowInput {

    public LoanInput {
        Objects.requireNonNull(valuationDate, "valuationDate");
        Objects.requireNonNull(disbursementDate, "disbursementDate");
        Objects.requireNonNull(schedule, "schedule");
        installmentFees = List.copyOf(Objects.requireNonNull(installmentFees, "installmentFees"));
        if (principal <= 0) {
            throw new IllegalArgumentException("principal must be > 0");
        }
        if (installmentFees.size() != schedule.flows().size()) {
            throw new IllegalArgumentException("one fee per installment: " + installmentFees.size() + " for "
                    + schedule.flows().size() + " installments");
        }
        if (upfrontFees < 0 || upfrontFees >= principal) {
            throw new IllegalArgumentException("upfront fees out of range");
        }
    }
}
