/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.softcaster.master_data_mgr.AppTreeItem;

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
        // Foglie
        securities.add(new DefaultMutableTreeNode(new MasterDataNode("Bonds", AppTreeItem.BOND)));
        securities.add(new DefaultMutableTreeNode(new MasterDataNode("Equities", AppTreeItem.EQUITY)));
        instruments.add(securities);

        DefaultMutableTreeNode forex = new DefaultMutableTreeNode("Forex");
        forex.add(new DefaultMutableTreeNode(new MasterDataNode("Currencies", AppTreeItem.CURRENCY)));
        forex.add(new DefaultMutableTreeNode(new MasterDataNode("Curr.Pairs", AppTreeItem.CURR_PAIR)));
        instruments.add(forex);

        DefaultMutableTreeNode derivatives = new DefaultMutableTreeNode("Derivatives");
        DefaultMutableTreeNode futures = new DefaultMutableTreeNode("Futures");
        futures.add(new DefaultMutableTreeNode(new MasterDataNode("Bond Futures", AppTreeItem.BOND_FUTURE)));
        futures.add(new DefaultMutableTreeNode(new MasterDataNode("Mm Futures", AppTreeItem.MM_FUTURE)));
        futures.add(new DefaultMutableTreeNode(new MasterDataNode("Fx Futures", AppTreeItem.FX_FUTURE)));
        futures.add(new DefaultMutableTreeNode(new MasterDataNode("Cmd Futures", AppTreeItem.CMD_FUTURE)));
        derivatives.add(futures);

        DefaultMutableTreeNode options = new DefaultMutableTreeNode("Options");
        options.add(new DefaultMutableTreeNode(new MasterDataNode("Bond Options", AppTreeItem.BOND_OPTION)));
        options.add(new DefaultMutableTreeNode(new MasterDataNode("MM Options", AppTreeItem.MM_OPTION)));
        options.add(new DefaultMutableTreeNode(new MasterDataNode("Fx Options", AppTreeItem.FX_OPTION)));
        derivatives.add(options);

        instruments.add(derivatives);

        root.add(instruments);

        // References
        DefaultMutableTreeNode references = new DefaultMutableTreeNode("References");
        references.add(new DefaultMutableTreeNode(new MasterDataNode("Counterparts",AppTreeItem.COUNTERPARTY)));
        references.add(new DefaultMutableTreeNode(new MasterDataNode("Issuers",AppTreeItem.ISSUER)));
        references.add(new DefaultMutableTreeNode(new MasterDataNode("Portfolios",AppTreeItem.PORTFOLIO)));
        references.add(new DefaultMutableTreeNode(new MasterDataNode("Positions",AppTreeItem.POSITION)));
        root.add(references);

        DefaultTreeModel model = new DefaultTreeModel(root);
        return model;
    }
}
