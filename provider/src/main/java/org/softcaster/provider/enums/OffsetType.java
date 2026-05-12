/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.provider.enums;

/**
 *
 * @author svil
 */
public enum OffsetType implements IdentifiableEnum {
    DAYS(1, "DAYS", "Days"),
    MOUNTHS(2, "MOUNTHS", "Mounths"),
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
}

/*
    // Cache the values to avoid array cloning on every call
    private static final Offset[] ENUMS = Offset.values();

    public static Offset fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        }
        return ENUMS[ordinal];
    }


*/
