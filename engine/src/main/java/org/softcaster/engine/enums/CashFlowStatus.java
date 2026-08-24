/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum CashFlowStatus implements IdentifiableEnum {

    ESTIMATED(1, "ESTIMATED", "Estimated"),
    RECORDED(2, "RECORDED", "Recorded"),
    PAID(3, "PAID", "Paid");

    private final int id;
    private final String code;
    private final String description;

    CashFlowStatus(int id, String code, String description) {
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

    public static CashFlowStatus fromId(int id) {
        return IdentifiableEnum.fromId(CashFlowStatus.class, id);
    }

    public static CashFlowStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(CashFlowStatus.class, code);
    }
}
