/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
//import org.softcaster.marketdataprovider.euronext.EuroNextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ph.alephzero.finance.BondFwdPriceRequest;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.forward.BondForward;
import ph.alephzero.finance.products.forward.BondForwardInputData;
import ph.alephzero.finance.products.forward.BondForwardOutputData;

/**
 *
 * @author ep
 */
@Service("bondForwardCalculator")
public class BondForwardCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;

    @Autowired
    @Qualifier("bondForward")
    private BondForward calculator;

    public BondForwardOutputData bondFwdValuation(BondFwdPriceRequest request) {

        BondForwardInputData input = new BondForwardInputData();

        // Carico anagrafica sottostante
        SecurityMasterData smd = smdDAO.findByIsin(request.getIsin());

        input.setDaycount(DayCountBasis.ACT_ACT);
        input.setMaturityDate(request.getMaturityDate());

        if (NumberUtils.isZero(request.getReferencePrice())) {
            //SEuroNextProvider provider = EuroNextProvider.getInstance();
            input.setSpotPrice(100./*provider.getBondQuote(smd.getIsin(), REQUEST_TYPE.MIDDLE)*/);
        } else {
            input.setSpotPrice(request.getReferencePrice());
        }

        // Tasso free-risk
        input.setRate(request.getRepoRate());

        input.setCompounding(Compounding.COMPOUNDED);
        input.setSettlementDate(CalendarHelper.getNextBusinessDate(request.getReferenceDate(),
                smd.getCalendar(), smd.getBusinessDays()));

        if (!smd.getCashFlows().isEmpty()) {
            List<ph.alephzero.finance.cashflows.CashFlowItem> cashFlows = new ArrayList<>();
            for (CashFlowItem item : smd.getCashFlows()) {
                ph.alephzero.finance.cashflows.CashFlowItem cashFlowItem = new ph.alephzero.finance.cashflows.CashFlowItem();
                cashFlowItem.setStart(item.getStartDate());
                cashFlowItem.setEnd(item.getEnddate());
                cashFlowItem.setInterest(item.getInterest());
                cashFlowItem.setAmount(item.getAmount());
                cashFlowItem.setDiscountFactors(request.getRepoRate());
                cashFlows.add(cashFlowItem);
            }
            input.setUnderliyngCashFlows(cashFlows);
        }

        BondForwardOutputData output = calculator.valuation(input);

        return output;
    }

}
