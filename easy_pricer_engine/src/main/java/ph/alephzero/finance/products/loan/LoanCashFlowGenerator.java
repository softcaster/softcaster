package ph.alephzero.finance.products.loan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.BasicCashFlows;
import ph.alephzero.finance.cashflows.BasicDatedCashFlows;
import ph.alephzero.finance.cashflows.CashFlowCalculator;
import ph.alephzero.finance.cashflows.CashFlowItem;
import ph.alephzero.finance.cashflows.CashFlows;
import ph.alephzero.finance.cashflows.MergedCashFlows;
import ph.alephzero.finance.cashflows.MergedDatedCashFlows;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @ipc:calculator-class
 * @author jon
 *
 */
public final class LoanCashFlowGenerator {

    /**
     * Compute annuity amount from principal, rate and number of periods.
     *
     * Assumes compounding date is the same as equal payment date, and payments
     * are in arrears. Interest rate should be that which is applicable for a
     * single period. E.g. if period is in months, and rate is annualized, then
     * caller should de-annualize first.
     *
     * First cf is for time 0 and is negative the principal.
     *
     * @ipc:calculation
     * @param principal principal of the loan
     * @param rate interest rate per period
     * @param periods number of payment/compounding periods.
     * @return amortization cash flow
     */
    public static CashFlows annuity(double principal, double rate, int periods) {
        double annuity = CashFlowCalculator.annuities(rate, periods, principal, 0, true);
        double amort, interest;

        BasicCashFlows prin = new BasicCashFlows();
        BasicCashFlows intr = new BasicCashFlows();

        prin.add(-principal);
        intr.add(0.0);

        for (int i = 1; i <= periods; i++) {
            interest = principal * rate;
            amort = annuity - interest;
            intr.add(interest);
            prin.add(amort);
            principal -= amort;
        }

        MergedCashFlows totalCF = new MergedCashFlows();
        totalCF.merge(prin, "PRINCIPAL");
        totalCF.merge(intr, "INTEREST");
        return totalCF;
    }

    /**
     * Given annuity, generate cash flow until principal goes to 0.
     *
     * @ipc:calculation
     * @param principal
     * @param rate
     * @param annuity
     * @return
     */
    public static CashFlows annuityVariableTerm(double principal, double rate, double annuity) {
        double amort, interest;

        BasicCashFlows prin = new BasicCashFlows();
        BasicCashFlows intr = new BasicCashFlows();

        prin.add(-principal);
        intr.add(0.0);

        while (principal > 0.0) {
            interest = principal * rate;
            if (interest > annuity) {
                throw new IllegalArgumentException("Annuity less than period's interest. "
                        + "Loan can never be paid off. Check rate if it is de-annualized.");
            }
            amort = Math.min(annuity - interest, principal);
            intr.add(interest);
            prin.add(amort);
            principal -= amort;
        }

        MergedCashFlows totalCF = new MergedCashFlows();
        totalCF.merge(prin, "PRINCIPAL");
        totalCF.merge(intr, "INTEREST");
        return totalCF;
    }

    /**
     * Simple add-on loan: balloon payment, no compounding, ACT/360. This is
     * usually the case for short-term simple add-on loans.
     *
     * @ipc:calculation
     * @see #simpleAddOn(Date, Date, double, double, DayCountBasis)
     * @param settlement
     * @param maturity
     * @param principal
     * @param rate
     * @return
     */
    public static CashFlows simpleAddOn(Date settlement, Date maturity, double principal, double rate) {
        return simpleAddOn(settlement, maturity, principal, rate, DayCountBasis.ACT_360);
    }

    /**
     * Simple add-on loan: balloon payment, no compounding.
     *
     * @ipc:calculation
     * @param settlement settlement/value date
     * @param maturity maturity date
     * @param principal principal amount
     * @param rate interest rate in decimal
     * @param basis day count basis
     * @return
     */
    public static CashFlows simpleAddOn(Date settlement, Date maturity, double principal, double rate, DayCountBasis basis) {
        if (settlement.compareTo(maturity) > 0) {
            throw new IllegalArgumentException("Settlement (value) date must be before maturity date.");
        }

        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows intr = new BasicDatedCashFlows(settlement);

        prin.add(settlement, -principal);

        double timeFactor = DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, settlement);
        prin.add(maturity, principal);
        intr.add(maturity, principal * rate * timeFactor);

        MergedDatedCashFlows out = new MergedDatedCashFlows(settlement);
        out.merge(prin, "PRINCIPAL");
        out.merge(intr, "INTEREST");
        return out;
    }

    /**
     * Simple discounted loan: balloon payment, no compounding, ACT/360.
     *
     * @ipc:calculation
     * @param settlement
     * @param maturity
     * @param par
     * @param rate
     * @return
     */
    public static CashFlows simpleDiscounted(Date settlement, Date maturity, double par, double rate) {
        return simpleDiscounted(settlement, maturity, par, rate, DayCountBasis.ACT_360);
    }

    /**
     * Simple discounted loan: balloon payment, no compounding.
     *
     * @ipc:calculation
     * @see #simpleDiscounted(Date, Date, double, double, DayCountBasis)
     * @param settlement settlement/value date
     * @param maturity maturity date
     * @param par total amount at maturity
     * @param rate interest rate in decimal
     * @param basis day count basis
     * @return
     */
    public static CashFlows simpleDiscounted(Date settlement, Date maturity, double par, double rate, DayCountBasis basis) {
        if (settlement.compareTo(maturity) > 0) {
            throw new IllegalArgumentException("Settlement (value) date must be before maturity date.");
        }

        BasicDatedCashFlows prin = new BasicDatedCashFlows(settlement);
        BasicDatedCashFlows intr = new BasicDatedCashFlows(settlement);

        double timeFactor = DateUtil.diffDays(settlement, maturity, basis) * 1.0 / DateUtil.daysOfYear(basis, settlement);
        double prin0 = par / (1 + rate * timeFactor);
        prin.add(settlement, -prin0);

        prin.add(maturity, prin0);
        intr.add(maturity, par - prin0);

        MergedDatedCashFlows out = new MergedDatedCashFlows(settlement);
        out.merge(prin, "PRINCIPAL");
        out.merge(intr, "INTEREST");
        return out;
    }

    // Piano ammortamento francese
    public static List<CashFlowItem> standardAmortizationSchedule(LoanInfo info) {
        
        info.initialize();
        
        List<CashFlowItem> cashFlows = new ArrayList<>();
        double principal = 0;
        int k = 1;
        org.softcaster.commons.types.Date paymentDate = new org.softcaster.commons.types.Date(info.start);
        CashFlowItem item = null;

        for (int i = 0; i < info.totalNop; i++) {
            item = new CashFlowItem();
            item.setEnd(paymentDate.utilDate());
            principal = info.getPrincipal(k);
            item.setAmount(principal);
            item.setInterest(info.initialInstallment - principal);
            cashFlows.add(item);
            k++;
            paymentDate.addMonths(12/info.nop);
        }

        return cashFlows;
    }

    // Piano ammortamento italiano
    public static List<CashFlowItem> straightLinePrincipal(LoanInfo info) {
        
        info.initialize();
        
        double principal = info.amount / info.totalNop;
        double remainingBalance = info.amount;
        org.softcaster.commons.types.Date paymentDate = new org.softcaster.commons.types.Date(info.start);

        List<CashFlowItem> cashFlows = new ArrayList<>();
        CashFlowItem item = null;

        for (int i = 0; i < info.totalNop; i++) {
            item = new CashFlowItem();
            item.setEnd(paymentDate.utilDate());
            item.setAmount(principal);
            item.setInterest(remainingBalance*info.nopRate);
            cashFlows.add(item);
            remainingBalance -= principal;
            paymentDate.addMonths(12/info.nop);
        }

        return cashFlows;
    }

    // Piano Interest Only Loan (Bullet)
    public static List<CashFlowItem> bullet(LoanInfo info) {
    
        info.initialize();

        List<CashFlowItem> cashFlows = new ArrayList<>();
        CashFlowItem item = null;
        org.softcaster.commons.types.Date paymentDate = new org.softcaster.commons.types.Date(info.start);

        for (int i = 0; i < info.totalNop; i++) {
            item = new CashFlowItem();
            item.setEnd(paymentDate.utilDate());
            item.setAmount(0);
            item.setInterest(info.amount*info.nopRate);
            cashFlows.add(item);
            paymentDate.addMonths(12/info.nop);
        }

        // Aggiungo importo bullet
        cashFlows.get(cashFlows.size() -1 ).setAmount(info.amount);
        
        return cashFlows;
    }
}
