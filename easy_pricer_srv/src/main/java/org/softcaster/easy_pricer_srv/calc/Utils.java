/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.Daycount;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author ep
 */
public class Utils {

    public static List<CashFlow> covertCashFlow(List<CashFlowItem> dbCashFlow) {
        List<CashFlow> flows = new ArrayList<>();

        for (org.softcaster.core.data.CashFlowItem item : dbCashFlow) {
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
        return flows;
    }

    public static DaycountBasis covertDaycount(Daycount dbDaycount) {
        return DaycountBasis.fromCode(dbDaycount.getCode());
    }

    public static Frequency covertFrequency(org.softcaster.core.data.Frequency dbFrequency) {
        return Frequency.fromCode(dbFrequency.getCode());
    }

    public static Compounding covertCompounding(String compounding) {
        return Compounding.fromCode(compounding);
    }

}
