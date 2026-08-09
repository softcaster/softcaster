/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.ui.models;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.softcaster.commons.ui.model.FndtNode;
import org.softcaster.easy_pricer_eod.AppTreeItem;

/**
 *
 * @author ep
 */
public class TreeModel {

    public static DefaultTreeModel buildTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Navigator");

        // Risk Engine
        DefaultMutableTreeNode services = new DefaultMutableTreeNode("Services");
        // Foglie
        services.add(new DefaultMutableTreeNode(new FndtNode<>("Trade Capture", AppTreeItem.REST_ENGINE)));
        services.add(new DefaultMutableTreeNode(new FndtNode<>("Trade Processing", AppTreeItem.TRADE_PROCESSOR)));
        services.add(new DefaultMutableTreeNode(new FndtNode<>("MtM Service", AppTreeItem.MTM_ENGINE)));
        services.add(new DefaultMutableTreeNode(new FndtNode<>("LifeCycle Scheduling", AppTreeItem.SCHEDULER)));
        services.add(new DefaultMutableTreeNode(new FndtNode<>("Accounting Processing", AppTreeItem.ACCT_ENGINE)));
        root.add(services);

        DefaultMutableTreeNode batches = new DefaultMutableTreeNode("Batches");
        // Foglie
        batches.add(new DefaultMutableTreeNode(new FndtNode<>("End Of Day", AppTreeItem.BATCH_EOD)));
        root.add(batches);
        
        DefaultMutableTreeNode config = new DefaultMutableTreeNode("Config");
        // Foglie
        config.add(new DefaultMutableTreeNode(new FndtNode<>("System Business Calendar", AppTreeItem.SBC)));
        root.add(config);

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }
}
