/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum FixingDayType implements IdentifiableEnum {

    CALENDAR_DAYS(1, "CALENDAR_DAYS", "Calendar Days"),
    BUSINESS_DAYS(2, "BUSINESS_DAYS", "Business Days");

    private final int id;
    private final String code;
    private final String description;

    FixingDayType(int id, String code, String description) {
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

    public static FixingDayType fromId(int id) {
        return IdentifiableEnum.fromId(FixingDayType.class, id);
    }

    public static FixingDayType fromCode(String code) {
        return IdentifiableEnum.fromCode(FixingDayType.class, code);
    }
}
