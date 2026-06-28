/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum TxnComponentType implements IdentifiableEnum {
    BROKER_FEE(1, "BROKER_FEE", "Broker Transaction and Execution Fees"),
    EXCHANGE_FEE(2, "EXCHANGE_FEE", "CME / Clearing House Regulatory Fees"),
    INITIAL_MARGIN(3, "INITIAL_MARGIN", "Initial Margin Deposit Requirement"),
    MAINTENANCE_MARGIN(4, "MAINTENANCE_MARGIN", "Maintenance Margin Requirement"),
    OPTION_PREMIUM(5, "OPTION_PREMIUM", "Option Premium Paid or Written"),
    BOND_ACCRUAL(6, "BOND_ACCRUAL", "Bond Accrual");

    private final int id;
    private final String code;
    private final String description;

    TxnComponentType(int id, String code, String description) {
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

    public static TxnComponentType fromId(int id) {
        return IdentifiableEnum.fromId(TxnComponentType.class, id);
    }

    public static TxnComponentType fromCode(String code) {
        return IdentifiableEnum.fromCode(TxnComponentType.class, code);
    }
}
