/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

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

    public static RequestType fromId(int id) {
        return IdentifiableEnum.fromId(RequestType.class, id);
    }

    public static RequestType fromCode(String code) {
        return IdentifiableEnum.fromCode(RequestType.class, code);
    }
}
