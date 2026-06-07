/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum NormalBalance implements IdentifiableEnum {
    DEBIT(1, "DEBIT", "Dr"),
    CREDIT(2, "CREDIT", "Cr");

    private final int id;
    private final String code;
    private final String description;

    NormalBalance(int id, String code, String description) {
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
    
    public static NormalBalance fromId(int id) {
        return IdentifiableEnum.fromId(NormalBalance.class, id);
    }

    public static NormalBalance fromCode(String code) {
        return IdentifiableEnum.fromCode(NormalBalance.class, code);
    }    
}
