/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum ProjectionMethod implements IdentifiableEnum {

    LAST_COUPON(1, "LAST_COUPON", "Last Coupon"),
    FORWARD_CURVE(2, "FORWARD_CURVE", "Forward Curve"),
    HIST_SERIE(3, "HIST_SERIE", "Historical Serie"),
    SHORT_BOND(4, "SHORT_BOND", "Short Bond"),
    NONE(5, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    ProjectionMethod(int id, String code, String description) {
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

    public static ProjectionMethod fromId(int id) {
        return IdentifiableEnum.fromId(ProjectionMethod.class, id);
    }

    public static ProjectionMethod fromCode(String code) {
        return IdentifiableEnum.fromCode(ProjectionMethod.class, code);
    }
}

