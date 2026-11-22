/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.PositionMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 * Nodo non postable della gerarchia delle posizioni (portafoglio, desk, ...).
 *
 * @author ep
 */
public class PortfolioBean implements IMasterDataModel {

    private final PositionMasterData node;

    public PortfolioBean(PositionMasterData node) {
        this.node = node;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                node.getCode();
            case 1 ->
                node.getDescription();
            case 2 ->
                node.getPositionType().getDescription();
            case 3 ->
                node.getParent() == null ? "" : node.getParent().getCode();
            case 4 ->
                node.getCurrency().getIsoCode();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Type", "Parent", "Currency"};
    }

    public PositionMasterData getNode() {
        return node;
    }

}
