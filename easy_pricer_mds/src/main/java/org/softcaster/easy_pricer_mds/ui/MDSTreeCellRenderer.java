/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.easy_pricer_mds.AppTreeItem;

/**
 *
 * @author softc
 */
public class MDSTreeCellRenderer extends DefaultTreeCellRenderer {

    ImageIcon securityIcon = new ImageIcon(getClass().getResource("/images/angular/post_add_16dp.png"));
    ImageIcon forexIcon = new ImageIcon(getClass().getResource("/images/angular/euro_16dp.png"));
    ImageIcon futureIcon = new ImageIcon(getClass().getResource("/images/angular/loyalty_16dp.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Chiama il super per mantenere lo stile standard (selezione, colori, ecc.)
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (leaf && userObject instanceof FndtNode data) {
            // Verifico il tipo esplicito
            if (data.getType() instanceof AppTreeItem item) {
                // Logica per cambiare l'icona in base al tipo
                switch (item) {
                    case CURR_PAIR ->
                        setIcon(forexIcon);
                    case BOND, EQUITY ->
                        setIcon(securityIcon);
                    case YC_UPDATE, YC_DEFINE ->
                        setIcon(forexIcon);
                    case BOND_FUTURE, MM_FUTURE, FX_FUTURE ->
                        setIcon(futureIcon);
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
