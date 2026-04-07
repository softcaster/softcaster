/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

/**
 *
 * @author softc
 */
public class MasterDataNode {
    private final String name;
    private final String type; // es: "BOND", "FUTURE", "EQUITY"

    public MasterDataNode(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    // Il JTree userà questo per il testo
    public String toString() { return name; } 
    public String getType() { return type; }
}
