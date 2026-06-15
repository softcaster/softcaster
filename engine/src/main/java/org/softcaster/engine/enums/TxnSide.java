/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum TxnSide implements IdentifiableEnum {
    BUY(1, "BUY", "Buy"),
    SELL(2, "SELL", "Sell");

    private final int id;
    private final String code;
    private final String description;

    TxnSide(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @return the code
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    public static TxnSide fromId(int id) {
        return IdentifiableEnum.fromId(TxnSide.class, id);
    }

    public static TxnSide fromCode(String code) {
        return IdentifiableEnum.fromCode(TxnSide.class, code);
    }
}
