/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author ep
 */
public class TreeModel {

    public static DefaultTreeModel buildTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Master Data");

        // Instrument
        DefaultMutableTreeNode instruments = new DefaultMutableTreeNode("Instruments");
        DefaultMutableTreeNode securities = new DefaultMutableTreeNode("Securities");
        securities.add(new DefaultMutableTreeNode("Bonds"));
        securities.add(new DefaultMutableTreeNode("Equities"));
        securities.add(new DefaultMutableTreeNode("Curr.Pairs"));        
        instruments.add(securities);
        
        DefaultMutableTreeNode derivatives = new DefaultMutableTreeNode("Derivatives");        
        DefaultMutableTreeNode futures = new DefaultMutableTreeNode("Futures");
        futures.add(new DefaultMutableTreeNode("Bond Futures"));
        futures.add(new DefaultMutableTreeNode("MM Futures"));
        futures.add(new DefaultMutableTreeNode("Fx Futures"));
        derivatives.add(futures);
        
        DefaultMutableTreeNode options = new DefaultMutableTreeNode("Options");
        options.add(new DefaultMutableTreeNode("Bond Options"));
        options.add(new DefaultMutableTreeNode("MM Options"));
        options.add(new DefaultMutableTreeNode("Fx Options"));
        derivatives.add(options);
        
        instruments.add(derivatives);
        
        root.add(instruments);
        
        // References
        DefaultMutableTreeNode references = new DefaultMutableTreeNode("References");
        references.add(new DefaultMutableTreeNode("Counterparts"));
        references.add(new DefaultMutableTreeNode("Portfolios"));
        references.add(new DefaultMutableTreeNode("Positions"));
        root.add(references);

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }
}
