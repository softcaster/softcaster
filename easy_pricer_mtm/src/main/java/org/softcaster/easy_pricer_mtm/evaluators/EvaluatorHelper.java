/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.Frequency;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorHelper {

    private final BondPricer bondPricer;

    // Iniezione tramite costruttore (best practice)
    public EvaluatorHelper(@Qualifier("bondPricer") BondPricer bondPricer) {
        this.bondPricer = bondPricer;
    }
    
    public XRBOutputData calculateXRB(MasterData masterData, double mktPrice, LocalDate officialDate) {
        XRBOutputData output = null;

        if (masterData != null) {
            if (masterData instanceof SecurityMasterData smd) {

                List<Currency> currencies = smd.getCurrencyList();
                if (currencies != null && !currencies.isEmpty()) {
                    LocalDate valuationDate = null;
                    Calendar calendar = new Calendar(currencies);
                    valuationDate = calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());

                    XRBInputData input = new XRBInputData();
                    input.setValuationDate(valuationDate);
                    input.setReferencePrice(mktPrice);
                    input.setFrequency(Frequency.fromCode(smd.getFrequency().getCode()));
                    input.setDaycount(smd.getAccrualDaycount());
                    input.setCompounding(Compounding.COMPOUNDED);

                    // Cash Flow
                    if (!smd.getCashFlows().isEmpty()) {
                        List<CashFlow> flows = new ArrayList<>();
                        for (org.softcaster.core.data.CashFlowItem item : smd.getCashFlows()) {
                            CashFlow flow = new CashFlow(
                                    item.getStartDate().toLocalDate(),
                                    item.getEnddate().toLocalDate(),
                                    item.getEnddate().toLocalDate(),
                                    item.getAmount(),
                                    item.getInterest(),
                                    0.,
                                    CashFlowStatus.RECORDED
                            );
                            flows.add(flow);
                        }
                        input.setFlows(flows);
                    }
                    output = bondPricer.calculate(input);
                }
            }
        }

        return output;
    }
}
