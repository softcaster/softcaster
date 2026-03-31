/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.marketdataprovider;

/**
 *
 * @author svil
 */
public enum OFFSET_TYPE {
    DAYS, MOUNTHS, YEARS;

    // Cache the values to avoid array cloning on every call
    private static final OFFSET_TYPE[] ENUMS = OFFSET_TYPE.values();

    public static OFFSET_TYPE fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        }
        return ENUMS[ordinal];
    }

}
