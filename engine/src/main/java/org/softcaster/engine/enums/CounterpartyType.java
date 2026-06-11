/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum CounterpartyType implements IdentifiableEnum {
    NATURAL_PERSON(1, "NATURAL_PERSON", "Individual Person"),
    LEGAL_ENTITY(2, "LEGAL_ENTITY", "Corporate Entity"),
    INVESTMENT_FUND(3, "INVESTMENT_FUND", "Investment Fund"),
    SOVEREIGN_PUBLIC(4, "SOVEREIGN_PUBLIC", "Sovereign Government"),
    CHOUSE(5, "CHOUSE", "Clearing Houses"),
    CUSTODIAN(6, "CUSTODIAN", "Custodians"); //Banche Depositarie

    private final int id;
    private final String code;
    private final String description;

    CounterpartyType(int id, String code, String description) {
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

    public static CounterpartyType fromId(int id) {
        return IdentifiableEnum.fromId(CounterpartyType.class, id);
    }

    public static CounterpartyType fromCode(String code) {
        return IdentifiableEnum.fromCode(CounterpartyType.class, code);
    }
}
