/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_mds_core.DiscountFactorNode;
import org.softcaster.easy_pricer_mds_core.MarketDataNotFoundException;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.bricks.Node;

public class YieldCurveHelper {

    public static List<DiscountFactorNode> getDiscountFactors(String curveId, MarketDataService marketDataService, List<LocalDate> maturities) {

        try {
            YieldCurve yc = marketDataService.getYieldCurve(curveId);
            List<DiscountFactorNode> nodes = new ArrayList<>();
            for (LocalDate maturity : maturities) {
                double df = yc.getDiscountFactor(maturity);
                nodes.add(new DiscountFactorNode(df, df, maturity));
            }
            return nodes;
        } catch (MarketDataNotFoundException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            System.out.println(ex.getLocalizedMessage());
            return null;
        }
    }

    public static List<CurveNodeInput> getCNIList(List<Node> nodes) {
        List<CurveNodeInput> cniList = new ArrayList<>();
        CurveNodeInput cni = null;
        for (Node node : nodes) {
            OffsetType offsetType = OffsetType.fromCode(node.getOffset().offsetType().getCode());
            Offset offset = new Offset(node.getOffset().step(), offsetType);
            DaycountBasis daycount = DaycountBasis.fromCode(node.getDaycount());
            Compounding compounding = Compounding.fromCode(node.getCompounding());
            cni = new CurveNodeInput(node.getSymbol(), offset, node.getData().bid(),
                    daycount, compounding);
            cniList.add(cni);
        }
        return cniList;
    }
}
