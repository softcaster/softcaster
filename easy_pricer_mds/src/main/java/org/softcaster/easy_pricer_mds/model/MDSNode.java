/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.model;

import org.softcaster.easy_pricer_mds.AppTreeItem;


/**
 *
 * @author softc
 */
public class MDSNode {
    private final String name;
    private final AppTreeItem type; // es: "BOND", "FUTURE", "EQUITY"

    public MDSNode(String name, AppTreeItem type) {
        this.name = name;
        this.type = type;
    }

    @Override
    // Il JTree userà questo per il testo
    public String toString() { return name; } 
    public AppTreeItem getType() { return type; }
}
