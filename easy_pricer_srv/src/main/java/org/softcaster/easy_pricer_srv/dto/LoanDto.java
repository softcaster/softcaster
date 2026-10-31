package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.FltLoanMasterData;
import org.softcaster.core.data.LoanMasterData;

/**
 * Mutuo per il client: termini e spese (il piano e la valutazione sono in
 * LoanPricingResponse).
 */
public record LoanDto(
        Integer masterDataId,
        String code,
        String description,
        String assetClass,
        String currency,
        double principalAmount,
        double annualRate,
        LocalDate disbursementDate,
        LocalDate firstPaymentDate,
        LocalDate maturityDate,
        String frequency,
        String amortization,
        String daycount,
        int preAmortizationPeriods,
        int installments,
        List<Fee> fees,
        Double spread,
        Double floor,
        Double cap) {

    public record Fee(String type, String basis, String timing, double value) {
    }

    /**
     * Da chiamare in transazione (valuta, spese e piano LAZY).
     */
    public static LoanDto of(LoanMasterData lmd) {
        FltLoanMasterData flt = org.hibernate.Hibernate.unproxy(lmd) instanceof FltLoanMasterData f ? f : null;
        return new LoanDto(lmd.getIdMasterData(), lmd.getCode(), lmd.getDescription(), lmd.getAssetClass().getCode(),
                lmd.getCurrency().getIsoCode(), lmd.getPrincipalAmount(), lmd.getInterestRate(),
                lmd.getIssueDate().toLocalDate(), lmd.getFirstPaymentLocalDate(), lmd.getMaturityDate().toLocalDate(),
                lmd.getFrequency().getCode(), lmd.getAmortizationSchedule().getCode(), lmd.getDaycount().getCode(),
                lmd.getPreAmortizationPeriods(), lmd.getCashFlows().size(),
                lmd.getFees().stream().map(f -> new Fee(f.getFeeType().getCode(), f.getFeeBasis().getCode(),
                        f.getFeeTiming().getCode(), f.getValue())).toList(),
                flt == null ? null : flt.getFloatingTerms().getSpread(),
                flt == null ? null : flt.getFloatingTerms().getFloor(),
                flt == null ? null : flt.getFloatingTerms().getCap());
    }
}
