/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.softcaster.easy_pricer_core.data.SecurityMasterData;

/**
 *
 * @author softc
 */
public class BondTableModel extends AbstractTableModel {

    // Bond list
    private List<SecurityMasterData> bondList = null;
    private final String columnNames[] = {
        "Isin", "Description", "Issue Date", "Issue Price", "Maturity", "Redemption Price", "Coupon"
    };

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (bondList != null && !bondList.isEmpty()) {
            return bondList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (bondList == null || rowIndex >= bondList.size()) {
            return null;
        }
        // Più veloce di listIterator
        SecurityMasterData bean = bondList.get(rowIndex);
        return switch (columnIndex) {
            case 0 ->
                bean.getIsin();
            case 1 ->
                bean.getIssueDescription();
            case 2 ->
                bean.getIssueDate();
            case 3 ->
                bean.getIssuePrice();
            case 4 ->
                bean.getMaturityDate();
            case 5 ->
                bean.getRedempionPrice();
            case 6 ->
                bean.getInterestRate();
            default ->
                null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Fondamentale per far funzionare correttamente i renderer (allineamento a dx per numeri)
        return switch (columnIndex) {
            case 3, 5, 6 ->
                Double.class;
            case 2, 4 ->
                java.sql.Date.class;
            default ->
                String.class;
        };
    }

    public SecurityMasterData getElementAt(int rowIndex) {
        if (bondList != null && rowIndex >= 0 && rowIndex < bondList.size()) {
            return bondList.get(rowIndex);
        }
        return null;
    }

    public void setBondList(List<SecurityMasterData> bondList) {
        this.bondList = bondList;

        // Segnala alla tabella che i dati del modello sono cambiati
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Restituisce false per impedire la modifica di qualsiasi cella
        // Se Description dovesse diventare editabile aggiungere
        //  return columnIndex == 1; // Solo la colonna 1 è editabile
        return false;
    }
}
