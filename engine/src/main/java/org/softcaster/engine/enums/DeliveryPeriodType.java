/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum DeliveryPeriodType implements IdentifiableEnum {

    YEAR(1, "YEAR", "Year Future - annual delivery contract"),
    QUARTER(2, "QUARTER", "Quarter Future - quarterly delivery contract"),
    MONTH(3, "MONTH", "Month Future - monthly delivery contract"),
    DAY(4, "DAY", "Daily delivery - post final cascading");

    private final int id;
    private final String code;
    private final String description;

    DeliveryPeriodType(int id, String code, String description) {
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

    public static DeliveryPeriodType fromId(int id) {
        return IdentifiableEnum.fromId(DeliveryPeriodType.class, id);
    }

    public static DeliveryPeriodType fromCode(String code) {
        return IdentifiableEnum.fromCode(DeliveryPeriodType.class, code);
    }
}
