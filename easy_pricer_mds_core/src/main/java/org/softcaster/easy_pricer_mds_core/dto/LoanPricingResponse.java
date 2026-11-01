package org.softcaster.easy_pricer_mds_core.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Valutazione di un mutuo nella valuta del mutuo, dal punto di vista di chi
 * presta, con il piano delle rate.
 */
public class LoanPricingResponse implements Serializable {

    public String code;
    public LocalDate valuationDate;
    public String discountSource;

    public double principal;
    public double annualRate;          // TAN, punti percentuali
    public double installment;
    public double outstandingPrincipal;
    public double accruedInterest;
    public LocalDate nextPaymentDate;
    public double nextPaymentAmount;

    public double fairValue;           // valore attuale delle rate future
    public double fairValuePercent;    // in % del debito residuo
    public double yield;               // tasso interno delle rate future al fair value
    public double modifiedDuration;
    public double dv01;

    public double taeg;                // punti percentuali
    public double upfrontFees;
    public double totalInterest;
    public double totalFees;

    public List<Installment> schedule;

    /**
     * Una rata del piano.
     */
    public record Installment(int number, LocalDate startDate, LocalDate endDate, LocalDate paymentDate,
            double principal, double interest, double fees, double total, double outstanding) {
    }
}
