/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.ui;

/**
 *
 * @author softc
 */
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

public class DecimalRenderer extends DefaultTableCellRenderer {
    // Stesso pattern di org.softcaster.commons.utils.Converter
    private final DecimalFormat formatter = new DecimalFormat("###,##0.00000");

    public DecimalRenderer() {
        // Allineamento a destra
        setHorizontalAlignment(JLabel.RIGHT); 
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Number) {
            value = formatter.format(value);
        }
        super.setValue(value);
    }
}
