/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    public org.softcaster.engine.curve.YieldCurve buildYieldCurve(String idCurve, List<CurveNodeInput> newInputs, LocalDate officialDate) {
        org.softcaster.core.data.YieldCurve dbCurve = yieldCurveDAO.findByCode(idCurve);
        if (dbCurve != null) {
            Currency currency = Currency.getInstance(dbCurve.getCurrency().getIsoCode());
            return new org.softcaster.engine.curve.YieldCurve(officialDate, currency, newInputs);
        } else {
            return null;
        }
    }

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
        CurveNodeInput cni = new CurveNodeInput(item.getRic(), offset, item.getBid(), daycount_, compounding);
        return cni;
    }

    private CurveNodeInput getCNI(Node node) {
        CurveNodeInput cni = null;
        OffsetType offsetType = OffsetType.fromCode(node.getOffset().offsetType().getCode());
        long step = node.getOffset().step();
        cni = new CurveNodeInput(node.getSymbol(), new Offset(step, offsetType), node.getData().bid(), DaycountBasis.ACT_360, Compounding.SIMPLE);
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

    public void saveOrUpdateCurve(String curveId, List<CurveNodeInput> newInputs) {

        if (newInputs == null || newInputs.isEmpty()) {
            return;
        }

        // Recupero yield curve
        org.softcaster.core.data.YieldCurve dbCurve = yieldCurveDAO.findByCode(curveId);
        if (dbCurve != null && dbCurve.getItems() != null) {

            // Mappa gli item attualmente presenti sul DB per una ricerca veloce O(1)
            // Usiamo come chiave la combinazione "step_code" (es. "3_MONTHS")
            Map<String, YieldCurveItem> existingDbItems = dbCurve.getItems().stream()
                    .collect(Collectors.toMap(
                            item -> item.getOffsetValue() + "_" + OffsetType.fromId(item.getOffsetType()),
                            item -> item
                    ));

            List<YieldCurveItem> updatedItems = new ArrayList<>();

            // 3. Allinea i dati finanziari con le entità DB
            for (CurveNodeInput node : newInputs) {
                String key = node.tenorOffset().step() + "_" + node.tenorOffset().offsetType().getCode();

                if (existingDbItems.containsKey(key)) {
                    // Il nodo esiste già a DB: aggiorna solo tasso e discount factor (UPDATE)
                    YieldCurveItem existingItem = existingDbItems.get(key);
                    existingItem.setAsk(node.rate());
                    existingItem.setBid(node.rate());
                    updatedItems.add(existingItem);
                } else {
                    // Il nodo è nuovo: crea una nuova entità (INSERT)
                    YieldCurveItem newItem = new YieldCurveItem();
                    newItem.setOffsetValue((short)node.tenorOffset().step());
                    newItem.setOffsetType((short)node.tenorOffset().offsetType().getId());
                    newItem.setAsk(node.rate());
                    newItem.setBid(node.rate());
                    newItem.setDaycount((short)node.daycount().getId());       
                    newItem.setCompounding((short)node.compounding().getId()); 
                    updatedItems.add(newItem);
                }
            }

            // 4. Applica la lista aggiornata per gestire le eventuali cancellazioni (Orphan Removal)
            dbCurve.getItems().clear();
            dbCurve.getItems().addAll(updatedItems);
        }

    }
}
