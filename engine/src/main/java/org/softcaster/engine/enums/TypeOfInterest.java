/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum TypeOfInterest implements IdentifiableEnum {
    FIXED(1, "FIXED", "Fixed Rate"),
    FLOATING(2, "FLOATING", "Floating Rate"),
    ZERO_COUPON(3, "ZERO-COUPON", "Zero Coupon"),
    INFLATION(4, "INFLATION", "Inflation Linked"),
    CONVERTIBLE(5, "CONVERTIBLE", "Convertible"),
    CALLABLE(6, "CALLABLE", "Callable"),
    NONE(100, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    TypeOfInterest(int id, String code, String description) {
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
}
