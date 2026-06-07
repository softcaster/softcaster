/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum AccountNature implements IdentifiableEnum {
    ASSET(1, "ASSET", "Asset"),
    LIABILITY(2, "LIABILITY", "Liability"),
    EQUITY(3, "EQUITY", "Equity"),   
    INCOME(4, "INCOME", "Income"),   
    EXPENSE(5, "EXPENSE", "Expense"),  
    MEMORANDUM(6, "MEMORANDUM", "Memorandum");   

    private final int id;
    private final String code;
    private final String description;

    AccountNature(int id, String code, String description) {
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
    
    public static AccountNature fromId(int id) {
        return IdentifiableEnum.fromId(AccountNature.class, id);
    }

    public static AccountNature fromCode(String code) {
        return IdentifiableEnum.fromCode(AccountNature.class, code);
    }    
}
