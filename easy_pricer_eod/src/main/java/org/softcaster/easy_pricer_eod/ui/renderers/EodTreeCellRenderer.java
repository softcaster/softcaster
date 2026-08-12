/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.ui.renderers;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.easy_pricer_eod.AppTreeItem;

/**
 *
 * @author softc
 */
public class EodTreeCellRenderer extends DefaultTreeCellRenderer {

    ImageIcon serviceIcon = new ImageIcon(getClass().getResource("/images/angular/file_open_16dp.png"));
    ImageIcon batchIcon = new ImageIcon(getClass().getResource("/images/angular/schedule_16dp.png"));
    ImageIcon configIcon = new ImageIcon(getClass().getResource("/images/angular/assignment_16dp.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Chiama il super per mantenere lo stile standard (selezione, colori, ecc.)
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (leaf && userObject instanceof FndtNode data) {
            // Logica per cambiare l'icona in base al tipo
            if (data.getType() instanceof AppTreeItem item) {
                switch (item) {
                    case REST_ENGINE, TRADE_PROCESSOR, MTM_ENGINE, SCHEDULER, ACCT_ENGINE ->
                        setIcon(serviceIcon);
                    case BATCH_EOD ->
                        setIcon(batchIcon);
                    case SBC ->
                        setIcon(configIcon);
                    default -> {
                        setIcon(getDefaultLeafIcon()); // Torna al "pallino" o icona di default
                    }
                }
            }
        } else if (!leaf) {
            // Se vuoi mantenere le cartelle standard per i nodi padre
            // puoi non fare nulla o forzare setOpenIcon/setClosedIcon
        }

        return this;
    }

}
