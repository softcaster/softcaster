/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum AccountingEventType implements IdentifiableEnum {

    TRADE_EXECUTED(1, "TRADE_EXECUTED", "Trade Executed"),
    TRADE_AMENDED(2, "TRADE_AMENDED", "Trade Amended"),
    TRADE_CANCELED(3, "TRADE_CANCELED", "Trade Cancelled"),
    MTM(4, "MTM", "Mtm"),
    COUPON(5, "COUPON", "Coupon"),
    ACCRUAL(6, "ACCRUAL", "Accrual"),
    SETTLEMENT(7, "SETTLEMENT", "Settlement"),
    MATURITY(8, "MATURITY", "Maturity'"),
    FX_REVALUATION(9, "FX_REVALUATION", "Fx Revaluation");

    private final int id;
    private final String code;
    private final String description;

    AccountingEventType(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

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
    
    public static AccountingEventType fromId(int id) {
        return IdentifiableEnum.fromId(AccountingEventType.class, id);
    }

    public static AccountingEventType fromCode(String code) {
        return IdentifiableEnum.fromCode(AccountingEventType.class, code);
    }

}
