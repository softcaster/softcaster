/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_srv.dto.PricingRequest;
import org.softcaster.engine.analytics.BondForwardPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.BondForwardInputData;
import org.softcaster.engine.dto.MarketOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
//import org.softcaster.marketdataprovider.euronext.EuroNextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("bondForwardCalculator")
public class BondForwardCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;

    @Autowired
    @Qualifier("bondFwdPricer")
    private BondForwardPricer bondForwardPricer;

    public MarketOutputData bondFwdValuation(PricingRequest request) {

        MarketOutputData output = null;
        BondForwardInputData input = new BondForwardInputData();

        String underlyingIsin = getCTD(request.isin);
        if (!underlyingIsin.isBlank()) {
            // Carico anagrafica sottostante
            SecurityMasterData smd = smdDAO.findByIsin(underlyingIsin);

            input.setSpotPrice(request.referencePrice);
            input.setValuationDate(request.referenceDate.toLocalDate());
            input.setDaycount(DaycountBasis.ACT_365);
            // Tasso free-risk
            input.setDomesticRate(0.1);
            input.setForeignRate(0.1);
            input.setMaturityDate(LocalDate.MIN);
            input.setCompounding(Compounding.COMPOUNDED);
            input.setConversionFactor(1.);

            if (!smd.getCashFlows().isEmpty()) {
                List<CashFlow> flows = new ArrayList<>();
                for (org.softcaster.core.data.CashFlowItem item : smd.getCashFlows()) {
                    CashFlow flow = new CashFlow(
                            item.getStartDate().toLocalDate(),
                            item.getEnddate().toLocalDate(),
                            item.getEnddate().toLocalDate(),
                            item.getAmount(),
                            item.getInterest(),
                            0.
                    );
                    flows.add(flow);
                }
                input.setUnderliyngCashFlows(flows);
            }

            //BondForwardOutputData output = calculator.valuation(input);
        }
        return output;
    }

    private String getCTD(String in) {
        return "";
    }

}
