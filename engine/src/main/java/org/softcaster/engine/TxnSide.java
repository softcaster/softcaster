/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine;

import org.softcaster.engine.enums.IdentifiableEnum;

/**
 *
 * @author softc
 */
public enum TxnSide implements IdentifiableEnum {
    BUY(1, "BUY", "Buy / Long Position"),
    SELL(2, "SELL", "Sell / Short Position");

    private final int id;
    private final String code;
    private final String description;

    TxnSide(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    // Metodi locali di delega rapida all'interfaccia generica
    public static TxnSide fromId(int id) {
        return IdentifiableEnum.fromId(TxnSide.class, id);
    }

    public static TxnSide fromCode(String code) {
        return IdentifiableEnum.fromCode(TxnSide.class, code);
    }

}
