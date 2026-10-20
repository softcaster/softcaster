/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum CurveRole implements IdentifiableEnum {

    /**
     * Discounting curve: converts future cash flows into present values.
     * Typically the risk-free / collateral (OIS, e.g. €STR) curve of the
     * currency.
     */
    DISCOUNT(1, "DISCOUNT", "Discount"),
    /**
     * Projection (forward) curve: estimates future fixings of a floating-rate
     * index (e.g. EURIBOR 6M) used to project the coupons of floating-rate
     * instruments. It is not used for discounting.
     */
    PROJECTION(2, "PROJECTION", "Projection"),
    /**
     * Repo curve: cost of financing a security through repurchase agreements.
     * Used to carry a bond forward (bond forwards, bond futures, basis
     * analysis).
     */
    REPO(3, "REPO", "Repo");

    private final int id;
    private final String code;
    private final String description;

    CurveRole(int id, String code, String description) {
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

    public static CurveRole fromId(int id) {
        return IdentifiableEnum.fromId(CurveRole.class, id);
    }

    public static CurveRole fromCode(String code) {
        return IdentifiableEnum.fromCode(CurveRole.class, code);
    }
}
