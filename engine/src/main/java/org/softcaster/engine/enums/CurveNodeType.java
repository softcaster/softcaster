/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum CurveNodeType implements IdentifiableEnum {

    MONEY_MARKET(1, "MONEY_MARKET", "Money Market"),
    SWAP(2, "SWAP", "Swap");

    private final int id;
    private final String code;
    private final String description;

    CurveNodeType(int id, String code, String description) {
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

    public static CurveNodeType fromId(int id) {
        return IdentifiableEnum.fromId(CurveNodeType.class, id);
    }

    public static CurveNodeType fromCode(String code) {
        return IdentifiableEnum.fromCode(CurveNodeType.class, code);
    }
}

