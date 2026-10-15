/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.helpers;

import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.Utils;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.FloatingCouponProjector;
import org.softcaster.engine.curve.YieldCurve;
import static org.softcaster.engine.enums.CouponProjectionMethod.LAST_COUPON;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("fltSecurityMasterDataHelper")
public class FltSecurityMasterDataHelper {

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    @Autowired
    CurrencyDAO currencyDAO;

    @Autowired
    YieldCurveEntityDAO yieldCurveEntityDAO;

    public List<CashFlow> completeFltSecurityCashFlow(FltSecurityMasterData fsmd) {
        List<CashFlow> cashFlows = null;
        if (fsmd.getCashFlows() != null && !fsmd.getCashFlows().isEmpty()) {
            switch (fsmd.getCouponPm()) {
                case LAST_COUPON ->
                    cashFlows = completeFromLastCoupon(fsmd);
                case FORWARD_CURVE ->
                    cashFlows = completeFromForwardCurve(fsmd);
                case HIST_SERIE ->
                    cashFlows = completeFromHistoricalSerie(fsmd);
                default -> {
                }
            }
        }
        return cashFlows;
    }

    private List<CashFlow> completeFromLastCoupon(FltSecurityMasterData fsmd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private List<CashFlow> completeFromForwardCurve(FltSecurityMasterData fsmd) {
        if (fsmd.getProjectionCurveId() != null) {
            String code = yieldCurveEntityDAO.getCodeFromId(fsmd.getProjectionCurveId());
            YieldCurve projectionCurve = marketDataService.getYieldCurve(code);
            if (projectionCurve == null) {
                String message = "No Projection Curve: " + code + " found";
                throw new IllegalArgumentException(message);
            }

            FloatingCouponProjector.Terms terms = new FloatingCouponProjector.Terms(fsmd.getSpread(),
                    fsmd.getFloor(), fsmd.getIndexMonths(), fsmd.getFixingLagDays(), DaycountBasis.ACT_360, Frequency.SEMI_ANNUAL);
            Currency ccy = currencyDAO.findByIsoCode(fsmd.getCurrency().getIsoCode());
            Calendar calendar = new Calendar(ccy);
            double outstandingBalance = fsmd.getRedempionPrice();
            List<CashFlow> estimatedCashFlows = FloatingCouponProjector.project(Utils.convertCashFlow(fsmd.getCashFlows()), projectionCurve,
                    marketDataService.getOfficialDate(), terms, outstandingBalance, (date, n) -> calendar.getNextBusinessDate(date, n));
            return estimatedCashFlows;
        } else {
            throw new IllegalArgumentException("Yield Curve is null");
        }
    }

    private List<CashFlow> completeFromHistoricalSerie(FltSecurityMasterData fsmd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
