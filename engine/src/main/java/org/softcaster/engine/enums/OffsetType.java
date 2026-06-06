/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.engine.enums;

/**
 *
 * @author svil
 */
public enum OffsetType implements IdentifiableEnum {
    DAYS(1, "DAYS", "Days"),
    MONTHS(2, "MONTHS", "Months"),
    YEARS(3, "YEARS", "Years"),
    NONE(4, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    OffsetType(int id, String code, String description) {
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

    public static OffsetType fromId(int id) {
        return IdentifiableEnum.fromId(OffsetType.class, id);
    }

    public static OffsetType fromCode(String code) {
        return IdentifiableEnum.fromCode(OffsetType.class, code);
    }
}
