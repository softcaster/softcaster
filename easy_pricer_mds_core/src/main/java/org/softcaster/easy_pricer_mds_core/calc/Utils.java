/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.Compounding;

/**
 *
 * @author ep
 */
public class Utils {

    public static List<CashFlow> convertCashFlow(List<CashFlowItem> dbCashFlow) {
        List<CashFlow> flows = new ArrayList<>();

        for (org.softcaster.core.data.CashFlowItem item : dbCashFlow) {
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
        return flows;
    }

    public static Compounding convertCompounding(String compounding) {
        return Compounding.fromCode(compounding);
    }

}
