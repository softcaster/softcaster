/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.NumberUtils;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlowItem;
import ph.alephzero.finance.cashflows.Schedule;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author softc
 */
public class BondForward {

    // Torna cedola piu capitalizzazione fino alla scadenza del future
    protected double getSingleCapitalizedCoupon(CashFlowItem item, java.sql.Date settlement, double rateS, java.sql.Date maturity, double rateL) {

        // Montante long
        int days = DateUtil.diffDays(settlement, maturity, DayCountBasis.ACT_365);
        double tenorL = days / DayCountBasis.ACT_365.getTime();
        double accumulationFactorL = Schedule.getAccumulationFactor(rateL, tenorL, Compounding.COMPOUNDED);

        // Montante short
        days = DateUtil.diffDays(settlement, item.getEnd(), DayCountBasis.ACT_365);
        double tenorS = days / DayCountBasis.ACT_365.getTime();
        double accumulationFactorS = Schedule.getAccumulationFactor(rateS, tenorS, Compounding.COMPOUNDED);

        // Montante fwd-fwd
        double accumulationFactorF = accumulationFactorL;
        if (!NumberUtils.isZero(accumulationFactorS)) {
            accumulationFactorF /= accumulationFactorS;
        }

        // Coupon piu capitalizzazione fino a scadenza future
        return item.getInterest() * accumulationFactorF;
    }

    protected double getCapitalizedCoupon(java.sql.Date settlementDate, java.sql.Date maturityDate, List<CashFlowItem> underliyngCashFlows, double rate) {
        double capitalizedCoupon = 0;
        if (underliyngCashFlows == null || underliyngCashFlows.isEmpty()) {
            return capitalizedCoupon;
        }

        Date _settlement = new Date(settlementDate);
        Date _maturity = new Date(maturityDate);

        Date endDate = null;
        Date startDate = null;
        CashFlowItem coupon = null;
        List<CashFlowItem> capitalizedFlows = new ArrayList<>();

        for (CashFlowItem item : underliyngCashFlows) {
            endDate = new Date(item.getEnd());
            startDate = new Date(item.getStart());
            // Se sono andato oltre, esco
            if (startDate.isGreaterOrEqualThan(_maturity)) {
                break;
            }
            if (endDate.isGreaterOrEqualThan(_settlement) && endDate.isLessOrEqualThan(_maturity)) {
                coupon = new CashFlowItem();
                coupon.setInterest(item.getInterest());
                coupon.setStart(startDate.sqlDate());
                coupon.setEnd(endDate.sqlDate());
                capitalizedFlows.add(coupon);
            }
        }

        for (CashFlowItem item : capitalizedFlows) {
            // Ipotesi di tassi piatti tra settlement e maturity, uso sempre input.getRate()
            capitalizedCoupon += getSingleCapitalizedCoupon(item, settlementDate, rate, maturityDate, rate);
        }

        // Se almeno una cedola cade nell'intervallo tra settlement e la
        // maturity del future, allora la successiva determina il rateo
        // che andra dalla decorrenza della cedola (corrispondente alla end
        // dell' ultima cedola nella lista)alla maturity del future
        if (capitalizedCoupon > 0) {
            // ottengo ultima cedola
            CashFlowItem lastCoupon = capitalizedFlows.get(capitalizedFlows.size() - 1);
            if (lastCoupon != null) {
                // Giorni cedola
                int totalDays = DateUtil.diffDays(lastCoupon.getStart(), lastCoupon.getEnd(), DayCountBasis.ACT_365);
                // Nota che la scadenza dell'ultima cedola della lista corrisponde
                // alla decorrenza della successiva
                int accrualDays = DateUtil.diffDays(lastCoupon.getEnd(), maturityDate, DayCountBasis.ACT_365);
                // trasformo in double moltiplicando per 1.0
                double tenor = accrualDays * 1.0 / totalDays;
                capitalizedCoupon += tenor * lastCoupon.getInterest();
            }
        }
        return capitalizedCoupon;
    }

    // Rateo d'interesse maturato dal titolo(CTD) alla data di scadenza del Future.
    // E' il caso della cedola che non cade tra settlement e maturity del future.
    // Devo trovare la cedola che "ricopre" l'intervallo tra settlement e maturity
    protected double getAccrual(java.sql.Date settlementDate, java.sql.Date maturityDate, List<CashFlowItem> underliyngCashFlows, boolean spotAccrual) {
        double accrual = 0;
        if (underliyngCashFlows == null || underliyngCashFlows.isEmpty()) {
            return accrual;
        }

        Date _settlement = new Date(settlementDate);
        Date _maturity = new Date(maturityDate);
        Date endDate = null;
        Date startDate = null;
        for (CashFlowItem item : underliyngCashFlows) {
            endDate = new Date(item.getEnd());
            startDate = new Date(item.getStart());
            // Trovata la cedola che ricopre l' intervallo settlement-maturity
            if (startDate.isLessOrEqualThan(_settlement) && endDate.isGreaterOrEqualThan(_maturity)) {
                // Giorni cedola
                int totalDays = DateUtil.diffDays(item.getStart(), item.getEnd(), DayCountBasis.ACT_365);
                int accrualDays = 0;
                if (spotAccrual) {
                    // rateo spot del sottostante
                    accrualDays = DateUtil.diffDays(item.getStart(), settlementDate, DayCountBasis.ACT_365);
                } else {
                    // Nota che la scadenza dell'ultima cedola della lista corrisponde
                    // alla decorrenza della successiva
                    accrualDays = DateUtil.diffDays(item.getStart(), maturityDate, DayCountBasis.ACT_365);

                }
                // trasformo in double moltiplicando per 1.0
                double tenor = accrualDays * 1.0 / totalDays;
                accrual = tenor * item.getInterest();
                // esco dal ciclo
                break;
            }
        }
        return accrual;
    }

    protected double theoreticalPrice(java.sql.Date settlementDate, java.sql.Date maturityDate, List<CashFlowItem> underliyngCashFlows,
            double spotCleanPrice, double rate, DayCountBasis daycount, Compounding compounding) {
        double thPrice = 0;
        if (!NumberUtils.isZero(daycount.getTime())) {

            double capitalizedCoupon = getCapitalizedCoupon(settlementDate, maturityDate, underliyngCashFlows, rate);

            // Se nessuna cedola cade tra settlement e maturity del future
            // calcolo rateo tra star della cedola e maturity del future
            double accrual = 0;
            if (NumberUtils.isZero(capitalizedCoupon)) {
                accrual = getAccrual(settlementDate, maturityDate, underliyngCashFlows, false);
            }

            int totalDays = DateUtil.diffDays(settlementDate, maturityDate, daycount);
            double tenor = totalDays / daycount.getTime();
            double accumulationFactor = Schedule.getAccumulationFactor(rate, tenor, compounding);
            // accrual sottostante
            double spotAccrual = getAccrual(settlementDate, maturityDate, underliyngCashFlows, true);
            double dirtyPrice = spotCleanPrice + spotAccrual;
            thPrice = (dirtyPrice * accumulationFactor - (capitalizedCoupon + accrual));
        }
        return thPrice;
    }

    public BondForwardOutputData valuation(BondForwardInputData input) {

        BondForwardOutputData output = new BondForwardOutputData();
        output.setTheoreticalPrice(theoreticalPrice(input.getSettlementDate(), input.getMaturityDate(), input.getUnderliyngCashFlows(),
                input.getSpotPrice(), input.getRate(), input.getDaycount(), input.getCompounding()));
        return output;
    }

}
