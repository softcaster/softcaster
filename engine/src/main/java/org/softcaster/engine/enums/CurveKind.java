/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum CurveKind implements IdentifiableEnum {

    YIELD(1, "YIELD", "Yield Curve"),
    SPREAD(2, "SPREAD", "Spreaded Curve");

    private final int id;
    private final String code;
    private final String description;

    CurveKind(int id, String code, String description) {
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

    public static CurveKind fromId(int id) {
        return IdentifiableEnum.fromId(CurveKind.class, id);
    }

    public static CurveKind fromCode(String code) {
        return IdentifiableEnum.fromCode(CurveKind.class, code);
    }
}
