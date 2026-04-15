/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.model;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.easy_pricer_mds.AppTreeItem;

/**
 *
 * @author ep
 */
public class TreeModel {

    public static DefaultTreeModel buildTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Navigator");

        // Instrument
        DefaultMutableTreeNode instruments = new DefaultMutableTreeNode("Price Update");
        DefaultMutableTreeNode securities = new DefaultMutableTreeNode("Securities");
        // Foglie
        securities.add(new DefaultMutableTreeNode(new FndtNode<>("Bonds", AppTreeItem.BOND)));
        securities.add(new DefaultMutableTreeNode(new FndtNode<>("Equities", AppTreeItem.EQUITY)));
        instruments.add(securities);

        DefaultMutableTreeNode forex = new DefaultMutableTreeNode("Forex");
        forex.add(new DefaultMutableTreeNode(new FndtNode<>("Curr.Pairs", AppTreeItem.CURR_PAIR)));
        instruments.add(forex);

        DefaultMutableTreeNode derivatives = new DefaultMutableTreeNode("Derivatives");
        DefaultMutableTreeNode futures = new DefaultMutableTreeNode("Futures");
        futures.add(new DefaultMutableTreeNode(new FndtNode<>("Bond Futures", AppTreeItem.BOND_FUTURE)));
        futures.add(new DefaultMutableTreeNode(new FndtNode<>("MM Futures", AppTreeItem.MM_FUTURE)));
        futures.add(new DefaultMutableTreeNode(new FndtNode<>("Fx Futures", AppTreeItem.FX_FUTURE)));
        derivatives.add(futures);

        instruments.add(derivatives);

        root.add(instruments);

        // YC
        DefaultMutableTreeNode references = new DefaultMutableTreeNode("Yield Curve");
        references.add(new DefaultMutableTreeNode(new FndtNode<>("Update",AppTreeItem.YC_UPDATE)));
        references.add(new DefaultMutableTreeNode(new FndtNode<>("Define",AppTreeItem.YC_DEFINE)));
        root.add(references);

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }
}
