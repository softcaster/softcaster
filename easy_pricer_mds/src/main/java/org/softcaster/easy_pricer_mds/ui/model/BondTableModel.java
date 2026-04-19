/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui.model;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.easy_pricer_mds.bean.BondBean;

/**
 *
 * @author softc
 */
public class BondTableModel extends FndtTableModel<BondBean> {

    public BondTableModel(BondBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<BondBean> newData) {

        for (int i = 0; i < newData.size(); i++) {
            if (i < this.data.size()) {
                BondBean cpbOld = this.data.get(i);
                BondBean cpbNew = newData.get(i);

                // Calcola la tendenza la salva nel nuovo bean
                cpbNew.setTrendBid(Double.compare(cpbNew.getBid(), cpbOld.getBid()));
                cpbNew.setTrendAsk(Double.compare(cpbNew.getAsk(), cpbOld.getAsk()));
            }
        }
        
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
