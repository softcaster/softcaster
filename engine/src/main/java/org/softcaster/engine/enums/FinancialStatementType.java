/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum FinancialStatementType implements IdentifiableEnum {
    BALANCE_SHEET(1, "BALANCE_SHEET", "Balance Sheet"),
    INCOME_STATEMENT(2, "INCOME_STATEMENT", "Income Statement"),
    OFF_BALANCE_SHEET(3, "OFF_BALANCE_SHEET", "Off Balance Sheet");   

    private final int id;
    private final String code;
    private final String description;

    FinancialStatementType(int id, String code, String description) {
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
    
    public static FinancialStatementType fromId(int id) {
        return IdentifiableEnum.fromId(FinancialStatementType.class, id);
    }

    public static FinancialStatementType fromCode(String code) {
        return IdentifiableEnum.fromCode(FinancialStatementType.class, code);
    }    
}
