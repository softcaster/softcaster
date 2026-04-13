/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.model;

import java.util.List;
import org.softcaster.easy_pricer_mds.bean.CurrencyPairBean;

/**
 *
 * @author softc
 */
public class CurrPairTableModel extends MDSTableModel<CurrencyPairBean> {

    public CurrPairTableModel(CurrencyPairBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<CurrencyPairBean> newData) {

        for (int i = 0; i < newData.size(); i++) {
            if (i < this.data.size()) {
                CurrencyPairBean cpbOld = this.data.get(i);
                CurrencyPairBean cpbNew = newData.get(i);

                // Calcola la tendenza la salva nel nuovo bean
                cpbNew.setTrendBid(Double.compare(cpbNew.getBid(), cpbOld.getBid()));
                cpbNew.setTrendAsk(Double.compare(cpbNew.getAsk(), cpbOld.getAsk()));
            }
        }
        
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
