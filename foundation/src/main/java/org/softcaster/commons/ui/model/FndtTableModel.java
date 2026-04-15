    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.ui.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author softc
 * @param <T> i vari beans specifici
 */
public class FndtTableModel<T extends IFndtModel> extends AbstractTableModel {

    protected final List<T> data = new ArrayList<>();
    private final T prototype; // Ci serve un "esempio" per leggere le colonne

    public FndtTableModel(T prototype) {
        this.prototype = prototype;
    }

    public void setData(List<T> newData) {
        this.data.clear();
        this.data.addAll(newData);
        fireTableDataChanged(); // Notifica alla JTable di aggiornarsi
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return prototype.getColumnNames().length;
    }

    @Override
    public String getColumnName(int column) {
        return prototype.getColumnNames()[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            return data.get(rowIndex).getValueAt(columnIndex);
        }
        return null;
    }

    public T getElementAt(int modelRowIndex) {
        if (modelRowIndex >= 0 && modelRowIndex < data.size()) {
            return data.get(modelRowIndex);
        }
        return null;
    }

    // Nelle tabelle specifiche bastera scrivere
    // table.setDefaultRenderer(Double.class, new DecimalRenderer());
    // senza dover specificare a quale colonna si riferisce il renderer
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Se la lista ha dati, prendiamo il tipo dal primo elemento
        if (!data.isEmpty()) {
            Object value = getValueAt(0, columnIndex);
            if (value != null) {
                return value.getClass();
            }
        }
        // Altrimenti usiamo il prototipo o Object.class come fallback
        return Object.class;
    }
}
