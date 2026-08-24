/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum CouponProjectionMethod implements IdentifiableEnum {

    LAST_COUPON(1, "LAST_COUPON", "Last Coupon"),
    FORWARD_CURVE(2, "FORWARD_CURVE", "Forward Curve");

    private final int id;
    private final String code;
    private final String description;

    CouponProjectionMethod(int id, String code, String description) {
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

    public static CouponProjectionMethod fromId(int id) {
        return IdentifiableEnum.fromId(CouponProjectionMethod.class, id);
    }

    public static CouponProjectionMethod fromCode(String code) {
        return IdentifiableEnum.fromCode(CouponProjectionMethod.class, code);
    }
}
