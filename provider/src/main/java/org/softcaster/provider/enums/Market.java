/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.provider.enums;

import org.softcaster.commons.utils.IdentifiableEnum;

/**
 *
 * @author ep
 */
public enum Market implements IdentifiableEnum {
    CURRENCIES(1, "CURRENCIES", ""),
    BONDS(2, "BONDS", ""),
    EQUITIES(3, "EQUITIES", ""),
    FUTURES(4, "FUTURES", ""),
    COMMODITIES(5, "COMMODITIES", ""),
    RATES(6, "RATES", ""),
    NONE(7, "NONE", "");

    private final int id;
    private final String code;
    private final String description;

    Market(int id, String code, String description) {
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
