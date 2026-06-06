/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum CounterpartyRole implements IdentifiableEnum {
    CUSTOMER(1, "CUSTOMER", "Customer"),
    VENDOR(2, "VENDOR", "Vendor"),
    BROKER(3, "BROKER", "Financial Broker"),
    CLEARING_HOUSE(4, "CLEARING_HOUSE", "Clearing House"),
    INTERNAL_ENTITY(5, "INTERNAL_ENTITY", "Group companies / Affiliates");

    private final int id;
    private final String code;
    private final String description;

    CounterpartyRole(int id, String code, String description) {
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

    public static CounterpartyRole fromId(int id) {
        return IdentifiableEnum.fromId(CounterpartyRole.class, id);
    }

    public static CounterpartyRole fromCode(String code) {
        return IdentifiableEnum.fromCode(CounterpartyRole.class, code);
    }
}
