/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.ui;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.softcaster.master_data_mgr.models.MasterDataNode;

/**
 *
 * @author softc
 */
public class MasterDataTreeCellRenderer extends DefaultTreeCellRenderer {

    ImageIcon securityIcon = new ImageIcon(getClass().getResource("/images/angular/post_add_16dp.png"));
    ImageIcon forexIcon = new ImageIcon(getClass().getResource("/images/angular/subheader_16dp.png"));
    ImageIcon futureIcon = new ImageIcon(getClass().getResource("/images/angular/outdoor_garden_16dp.png"));
    ImageIcon optionIcon = new ImageIcon(getClass().getResource("/images/angular/subheader_16dp.png"));

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        // Chiama il super per mantenere lo stile standard (selezione, colori, ecc.)
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object userObject = node.getUserObject();

        if (leaf && userObject instanceof MasterDataNode data) {
            // Logica per cambiare l'icona in base al tipo
            switch (data.getType()) {
                case "BOND","EQUITY" ->
                    setIcon(securityIcon);
                case "CURRENCIES","CURR_PAIR" ->
                    setIcon(forexIcon);
                case "BOND_FUTURE","MM_FUTURE","FX_FUTURE" ->
                    setIcon(futureIcon);
                case "BOND_OPTION","MM_OPTION","FX_OPTION" ->
                    setIcon(optionIcon);
                default ->{
                    setIcon(getDefaultLeafIcon()); // Torna al "pallino" o icona di default
                }
            }
        } else if (!leaf) {
            // Se vuoi mantenere le cartelle standard per i nodi padre
            // puoi non fare nulla o forzare setOpenIcon/setClosedIcon
        }

        return this;
    }

}
