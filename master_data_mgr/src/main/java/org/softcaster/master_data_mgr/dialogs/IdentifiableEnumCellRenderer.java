/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.dialogs;

import javax.swing.*;
import java.awt.*;
import org.softcaster.engine.enums.IdentifiableEnum;

public class IdentifiableEnumCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        // Richiama il componente grafico di base (gestisce colori, selezioni, font)
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Se l'elemento implementa la nostra interfaccia, mostriamo la description
        if (value instanceof IdentifiableEnum identifiable) {
            setText(identifiable.getDescription()); // Es. "Individual / Natural Person"
        }

        return this;
    }
}
