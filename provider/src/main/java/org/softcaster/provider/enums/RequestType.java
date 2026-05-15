/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.provider.enums;

import org.softcaster.commons.utils.IdentifiableEnum;

/**
 *
 * @author svil
 */
public enum RequestType implements IdentifiableEnum {
    BID(1, "", ""),
    ASK(2, "", ""),
    MIDDLE(3, "", "");

    private final int id;
    private final String code;
    private final String description;

    RequestType(int id, String code, String description) {
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
