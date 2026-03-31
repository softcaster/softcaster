/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.cashflows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.softcaster.commons.utils.NumberUtils;
import ph.alephzero.finance.Compounding;
import static ph.alephzero.finance.Compounding.COMPOUNDED;
import static ph.alephzero.finance.Compounding.CONTINUOUS;
import static ph.alephzero.finance.Compounding.SIMPLE;
import static ph.alephzero.finance.Compounding.SIMPLE_THEN_COMPOUNDED;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.fixedincome.BondCashFlowGenerator;
import ph.alephzero.finance.util.DateUtil;
import ph.alephzero.finance.util.MathUtil;

/**
 *
 * @author ep
 */
public abstract class Schedule {

    protected final List<CashFlowItem> cashFlows;
    protected final DayCountBasis basis;
    protected final double principal;
    protected final int frequency;

    abstract static class Builder<T extends Builder<T>> {

        private Date issue;
        private Date maturity;
        private Date firstCoupon;
        private Date lastCoupon;
        private int frequency;
        private double couponRate;
        private double redemptionPrice;
        private DayCountBasis basis;

        // Subclasses must override this method to return "this"
        protected abstract T self();

        public T issue(Date val) {
            issue = val;
            return self();
        }

        public T maturity(Date val) {
            maturity = val;
            return self();
        }

        public T firstCoupon(Date val) {
            firstCoupon = val;
            return self();
        }

        public T lastCoupon(Date val) {
            lastCoupon = val;
            return self();
        }

        public T frequency(int val) {
            setFrequency(val);
            return self();
        }

        public T couponRate(double val) {
            couponRate = val;
            return self();
        }

        public T issuePrice(double val) {
            return self();
        }

        public T redemptionPrice(double val) {
            setRedemptionPrice(val);
            return self();
        }

        public T basis(DayCountBasis val) {
            setBasis(val);
            return self();
        }

        /**
         * @return the basis
         */
        public DayCountBasis getBasis() {
            return basis;
        }

        /**
         * @param basis the basis to set
         */
        public void setBasis(DayCountBasis basis) {
            this.basis = basis;
        }

        /**
         * @return the redemptionPrice
         */
        public double getRedemptionPrice() {
            return redemptionPrice;
        }

        /**
         * @param redemptionPrice the redemptionPrice to set
         */
        public void setRedemptionPrice(double redemptionPrice) {
            this.redemptionPrice = redemptionPrice;
        }

        /**
         * @return the frequency
         */
        public int getFrequency() {
            return frequency;
        }

        /**
         * @param frequency the frequency to set
         */
        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }

        abstract protected Schedule build();

        protected List<CashFlowItem> getCashFlows() {
            List<CashFlowItem> cashFlows = new ArrayList<>();
            CashFlows cf = BondCashFlowGenerator.cashFlowsOddBond(issue, issue, maturity, firstCoupon, lastCoupon, getRedemptionPrice(), couponRate, getFrequency(), getBasis());
            boolean first = true;
            Date start = null;
            Date end;
            for (Date date : cf.getDates()) {
                if (first) {
                    start = date;
                    first = false;
                } else {
                    end = date;
                    CashFlowItem item = new CashFlowItem();
                    item.setStart(start);
                    item.setEnd(end);
                    item.setInterest(cf.getCashFlow(end, "INTEREST"));
                    item.setAmount(cf.getCashFlow(end, "PRINCIPAL"));
                    cashFlows.add(item);
                    start = end;
                }
            }

            return cashFlows;
        }
    }

    Schedule(Builder<?> builder) {
        basis = builder.getBasis();
        frequency = builder.getFrequency();
        principal = builder.getRedemptionPrice();
        cashFlows = builder.getCashFlows();
    }

    public List<CashFlowItem> getCashFlows() {
        return Collections.unmodifiableList(cashFlows);
    }

    public double presentValue(final Date settlement) {
        return 0;
    }

    public double irr(final Date settlement, final double price, final double rate, final Compounding compounding) {
        MathUtil.Function1 f = new MathUtil.Function1() {

            @Override
            public double f(double x) {
                return f(x, compounding);
            }

            @Override
            public double f(double x, Compounding compounding) {
                double calcPrice = presentValue(settlement, x, compounding);
                return price - calcPrice;
            }
        };

        return MathUtil.rootNewton(f, rate, compounding);
    }

    public double durationMacaulay(Date settlement, double yield, Compounding compounding) {
        double duration = 0.0;
        double price = 0.0;

        for (CashFlowItem cf : cashFlows) {
            if (cf.getEnd().compareTo(settlement) > 0) {
                int totalDays = DateUtil.diffDays(settlement, cf.getEnd(), basis);
                double tenor = totalDays / 365.;
                double cf0 = cf.getAmount() + cf.getInterest();
                double df = getDiscountFactor(yield, tenor, compounding);

                duration += cf0 * tenor * df;
                price += cf0 * df;
            }
        }

        return duration / price;
    }

    public double durationModified(Date settlement, double yield, Compounding compounding) {
        return durationMacaulay(settlement, yield, compounding) / (1 + yield);
    }

    public static double getAccumulationFactor(double rate, double tenor, Compounding compounding) {
        double accumulationFactor = 0;

        switch (compounding) {
            case SIMPLE:
                accumulationFactor = (1 + rate * tenor);
                break;
            case CONTINUOUS:
                accumulationFactor = Math.exp(rate * tenor);
                break;
            case SIMPLE_THEN_COMPOUNDED:
                if (tenor < 1) {
                    return (getAccumulationFactor(rate, tenor, Compounding.SIMPLE));
                } else {
                    return (getAccumulationFactor(rate, tenor, Compounding.COMPOUNDED));
                }
            default:
            case COMPOUNDED:
                accumulationFactor = Math.pow((1 + rate), tenor);
                break;
        }

        return accumulationFactor;
    }

    public static double getDiscountFactor(double rate, double tenor, Compounding compounding) {

        double discontFactor = 0;

        if (NumberUtils.isZero(tenor)) {
            return discontFactor;
        }

        switch (compounding) {
            case SIMPLE:
                discontFactor = 1 / (1 + rate * tenor);
                break;
            case CONTINUOUS:
                discontFactor = 1 / Math.exp(rate * tenor);
                break;
            case SIMPLE_THEN_COMPOUNDED:
                if (tenor < 1) {
                    return (getDiscountFactor(rate, tenor, Compounding.SIMPLE));
                } else {
                    return (getDiscountFactor(rate, tenor, Compounding.COMPOUNDED));
                }
            default:
            case COMPOUNDED:
                discontFactor = 1 / Math.pow((1 + rate), tenor);
                break;
        }

        return discontFactor;
    }

    public double presentValue(Date settlement, final double rate, Compounding compounding) {
        double pv = 0;
        for (CashFlowItem cf : cashFlows) {
            if (cf.getEnd().compareTo(settlement) > 0) {
                int totalDays = DateUtil.diffDays(settlement, cf.getEnd(), basis);
                double tenor = totalDays / 365.;
                double df = getDiscountFactor(rate, tenor, compounding);
                pv += (cf.getInterest() + cf.getAmount()) * df;
            }
        }
        return pv;
    }

    public double accruedInterest(Date settlement) {

        if (settlement == null) {
            return 0.;
        }

        double accrual = 0.;
        for (CashFlowItem cf : cashFlows) {
            if (cf.getEnd().compareTo(settlement) > 0) {
                int totalDays = DateUtil.diffDays(cf.getStart(), cf.getEnd(), basis);
                int accrualDays = DateUtil.diffDays(cf.getStart(), settlement, basis);
                double tenor = accrualDays * 1.0 / totalDays;
                accrual = tenor * cf.getInterest();
                break;
            }
        }

        return accrual;
    }

    public void toCsv(Date settlement) {
        for (CashFlowItem cf : cashFlows) {
            if (settlement != null) {
                if (cf.getEnd().compareTo(settlement) < 0) {
                    continue;
                }
            }
            System.out.println(cf.toCsv());
        }

    }
}
