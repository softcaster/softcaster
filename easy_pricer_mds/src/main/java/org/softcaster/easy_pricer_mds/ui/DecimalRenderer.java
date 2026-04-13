/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui;

/**
 *
 * @author softc
 */
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.softcaster.easy_pricer_mds.bean.ITrendable;
import org.softcaster.easy_pricer_mds.model.MDSTableModel;

public class DecimalRenderer extends DefaultTableCellRenderer {

    // Stesso pattern di org.softcaster.commons.utils.Converter
    private final DecimalFormat formatter = new DecimalFormat("###,##0.00000");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        // 1. Inizializzazione standard
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // 2. Formattazione e Allineamento
        if (value instanceof Number) {
            setText(formatter.format(value));
        }
        
        setHorizontalAlignment(JLabel.RIGHT);

        // 3. Logica del colore
        if (table.getModel() instanceof MDSTableModel<?> model) {
            int modelRow = table.convertRowIndexToModel(row);
            Object element = model.getElementAt(modelRow);

            if (element instanceof ITrendable trendable && !isSelected) {
                int trend = trendable.getTrendForColumn(column);

                if (trend > 0) {
                    setForeground(new Color(0, 150, 0));
                } else if (trend < 0) {
                    setForeground(Color.RED);
                } else {
                    setForeground(table.getForeground());
                }
            }
        }

        return this;
    }
}
