/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 * Projects the unfixed coupons of a floating-rate bond (e.g. CCT: Euribor 6M +
 * spread) from a projection curve, i.e. the curve bootstrapped from Euribor
 * deposits and IRS (see CurveBootstrapper).
 *
 * The projection curve is only used to read forward rates. Discounting is done
 * with another curve (ECB + spread): never shift the projection curve with the
 * Italian spread, the coupon rate does not contain Italian risk.
 *
 * Simplifications: single-curve forwards (no basis between tenors), no
 * convexity or timing adjustment, floor valued at its intrinsic value only.
 *
 * Not compiled against your project: CashFlow, Frequency and
 * DaycountBasis.calculate(...) are used as I have seen them in your code; adapt
 * the points marked ADAPT.
 */
public final class FloatingCouponProjector {

    private FloatingCouponProjector() {
    }

    /**
     * Contract terms of the floating-rate bond.
     *
     * @param spread margin over the index, decimal (0,003 = 30 bp)
     * @param floor minimum coupon rate (applied to index + spread), or null for
     * none
     * @param indexMonths tenor of the index (6 for Euribor 6M)
     * @param fixingShiftDays days between the start of the accrual period and
     * the start of the index period. 0 if the index is set at the start of the
     * period; negative if it is set in arrears of the period start (e.g. -30 if
     * the Euribor of the month before is used): check the bond regulation
     * @param accrualDaycount day count of the coupon (from the bond regulation)
     * @param frequency coupon frequency, only needed by the day count
     * calculator
     */
    public record Terms(double spread, Double floor, int indexMonths, int fixingShiftDays,
            DaycountBasis accrualDaycount, Frequency frequency) {

    }

    /**
     * Returns a new list in which every coupon whose index is not fixed yet has
     * its interest recomputed. Coupons already fixed (index period starting on
     * or before the valuation date) are kept as stored.
     *
     * @param flows
     * @param projectionCurve
     * @param valuationDate
     * @param terms
     * @return
     */
    public static List<CashFlow> project(List<CashFlow> flows, DiscountCurve projectionCurve,
            LocalDate valuationDate, Terms terms) {
        List<CashFlow> out = new ArrayList<>(flows.size());
        for (CashFlow cf : flows) {
            LocalDate indexStart = cf.accrualStart().plusDays(terms.fixingShiftDays());
            if (!indexStart.isAfter(valuationDate)) {
                // rate already fixed: the stored interest is the actual coupon
                out.add(cf);                                   
                continue;
            }
            LocalDate indexEnd = indexStart.plusMonths(terms.indexMonths());

            // Euribor is a simple rate ACT/360 over its own tenor, whatever the bond day count is
            double forward = projectionCurve.getForwardRate(indexStart, indexEnd, DaycountBasis.ACT_360, Compounding.SIMPLE);

            double rate = forward + terms.spread();
            if (terms.floor() != null) {
                rate = Math.max(rate, terms.floor());
            }
            double yearFraction = terms.accrualDaycount().calculate(cf.accrualStart(), cf.accrualEnd(), terms.frequency());
            double interest = cf.outstandingBalance() * rate * yearFraction;                       

            out.add(new CashFlow(cf.accrualStart(), cf.accrualEnd(), cf.paymentDate(),
                    cf.principal(), interest, cf.outstandingBalance(), cf.cashFlowStatus()));       
        }
        return out;
    }
}
