/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import org.softcaster.core.data.Daycount;
import org.softcaster.core.data.DaycountDAO;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.core.data.YieldCurveItem;
import org.softcaster.engine.curve.CurveNodeInput;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.bricks.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("yieldCurveBuilder")
public class YieldCurveBuilder {

    @Autowired
    YieldCurveDAO yieldCurveDAO;

    @Autowired
    DaycountDAO daycountDAO;

    public YieldCurve buildYieldCurve(IMarketDataProvider provider, String idCurve, LocalDate officialDate) {
        YieldCurve newYieldCurve = null;

        org.softcaster.core.data.YieldCurve dbCurve = yieldCurveDAO.findByCode(idCurve);
        if (dbCurve != null) {
            Currency currency = Currency.getInstance(dbCurve.getCurrency().getIsoCode());
            List<CurveNodeInput> input = new ArrayList<>();
            CurveNodeInput node = null;
            List<org.softcaster.core.data.YieldCurveItem> ycItems = dbCurve.getItems();
            if (ycItems != null) {
                for (org.softcaster.core.data.YieldCurveItem item : ycItems) {
                    node = getNode(item);
                    if (node != null) {
                        input.add(node);
                    }
                }
                newYieldCurve = new YieldCurve(officialDate, currency, input);
            }
        }

        return newYieldCurve;
    }

    private CurveNodeInput getNode(YieldCurveItem item) {

        OffsetType offsetType = OffsetType.fromId(item.getOffsetType());
        Offset offset = new Offset(item.getOffsetValue(), offsetType);
        Daycount daycount = daycountDAO.findByIdDaycount(item.getDaycount().intValue());
        DaycountBasis daycount_ = DaycountBasis.fromCode(daycount.getCode());
        Compounding compounding = Compounding.fromOrdinal(item.getCompounding());
        CurveNodeInput cni = new CurveNodeInput(offset, item.getBid(), daycount_, compounding);
        return cni;
    }

    private CurveNodeInput getCNI(Node node) {
        CurveNodeInput cni = null;
        OffsetType offsetType = OffsetType.fromCode(node.getOffset().offsetType().getCode());
        long step = node.getOffset().step();
        cni = new CurveNodeInput(new Offset(step,offsetType), node.getData().bid(), DaycountBasis.ACT_360, Compounding.SIMPLE);
        return cni;
    }

    List<CurveNodeInput> getNewInput(IMarketDataProvider provider, String curveId) {
        List<CurveNodeInput> newInput = null;
        List<Node> nodes = provider.getYieldCurveNodes(curveId);
        if (nodes != null && !nodes.isEmpty()) {
            newInput = new ArrayList<>();
            CurveNodeInput cni;
            for (Node n : nodes) {
                cni = getCNI(n);
                if (cni != null) {
                    newInput.add(cni);
                }
            }
        }
        return newInput;
    }
}
