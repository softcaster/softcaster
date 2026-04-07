/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.ui;

/**
 *
 * @author softc
 */
public class ZebraTable extends javax.swing.JTable {

    public ZebraTable() {
        super();
    }

    public ZebraTable(javax.swing.table.TableModel dm) {
        super(dm);
    }

    @Override
    public java.awt.Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
        java.awt.Component c = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? java.awt.Color.WHITE : new java.awt.Color(240, 240, 240));
        }

        // 2. AGGIUNGE IL MARGINE
        // Questo aggiunge 10 pixel di spazio a destra e a sinistra del testo
        if (c instanceof javax.swing.JComponent jc) {
            jc.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }
        return c;
    }
}
