/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum SettlementType implements IdentifiableEnum {

    PHYSICAL(1, "PHYSICAL", "Physical Settlement"),
    CASH(2, "CASH", "Cash Settlement");

    private final int id;
    private final String code;
    private final String description;

    SettlementType(int id, String code, String description) {
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
    
    public static SettlementType fromId(int id) {
        return IdentifiableEnum.fromId(SettlementType.class, id);
    }

    public static SettlementType fromCode(String code) {
        return IdentifiableEnum.fromCode(SettlementType.class, code);
    }
}
