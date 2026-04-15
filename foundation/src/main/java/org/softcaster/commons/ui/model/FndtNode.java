/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.ui.model;



/**
 *
 * @author softc
 * @param <T>
 */
// Il vincolo "extends Enum<T>" garantisce che T sia un enumerato
// Es Istanza con AssetType:
// FndtNode<AssetType> node1 = new FndtNode<>("Bund 10Y", AssetType.BOND);
public class FndtNode<T extends Enum<T>> {
    private final String name;
    private final T type; // es: "BOND", "FUTURE", "EQUITY"

    public FndtNode(String name, T type) {
        this.name = name;
        this.type = type;
    }

    // Il JTree userà questo per il testo
    @Override
    public String toString() { return name; } 

    public T getType() { return type; }
}
