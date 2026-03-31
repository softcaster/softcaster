/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

import org.softcaster.commons.utils.NumberUtils;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.Schedule;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author ep
 */
public class ForexForward {

    // Uso due daycount perchè le convenzioni sono diverse tra divise, es
    // EUR e USD ACT/360
    // GBP  ACT/365
    protected double forwardPoints(java.sql.Date settlementDate, java.sql.Date maturityDate,
            double spotPrice, double bcyRate, double ccyRate, DayCountBasis daycountBcy, DayCountBasis daycountCcy,
            Compounding compounding) {
        double fwdPoints = 0;
        double timeBcy = daycountBcy.getTime();
        double timeCcy = daycountCcy.getTime();
        if (!NumberUtils.isZero(timeBcy) && !NumberUtils.isZero(timeCcy)) {
            int totalDays = DateUtil.diffDays(settlementDate, maturityDate, daycountBcy);
            double tenorBcy = totalDays / timeBcy;
            double tenorCcy = totalDays / timeCcy;

            double accumulationFactorBcy = Schedule.getAccumulationFactor(bcyRate, tenorBcy, compounding);

            double deltaRates = (ccyRate * tenorCcy - bcyRate * tenorBcy);
            fwdPoints = spotPrice * (deltaRates / accumulationFactorBcy);
        }

        return fwdPoints;
    }

    protected double theoreticalPrice(java.sql.Date settlementDate, java.sql.Date maturityDate,
            double spotPrice, double bcyRate, double ccyRate, DayCountBasis daycountBcy, DayCountBasis daycountCcy,
            Compounding compounding) {
        double thPrice = 0;

        double timeBcy = daycountBcy.getTime();
        double timeCcy = daycountCcy.getTime();
        if (!NumberUtils.isZero(timeBcy) && !NumberUtils.isZero(timeCcy)) {
            int totalDays = DateUtil.diffDays(settlementDate, maturityDate, daycountBcy);
            double tenorBcy = totalDays / timeBcy;
            double tenorCcy = totalDays / timeCcy;

            // Formula parita coperta
            double accumulationFactorBcy = Schedule.getAccumulationFactor(bcyRate, tenorBcy, compounding);
            double accumulationFactorCcy = Schedule.getAccumulationFactor(ccyRate, tenorCcy, compounding);
            thPrice = spotPrice * (accumulationFactorCcy / accumulationFactorBcy);
        }
        return thPrice;
    }

    public ForexFwdOutputData valuation(ForexFwdInputData input) {

        ForexFwdOutputData output = new ForexFwdOutputData();

        output.setTheoreticalPrice(theoreticalPrice(input.settlementDate, input.maturityDate, input.spotPrice,
                input.rate, input.rateCcy, input.daycount, input.getDaycountCcy(), input.compounding));

        output.setForwardPoints(forwardPoints(input.settlementDate, input.maturityDate, input.spotPrice,
                input.rate, input.rateCcy, input.daycount, input.getDaycountCcy(), input.compounding));

        return output;
    }
}
