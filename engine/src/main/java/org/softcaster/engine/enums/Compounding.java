/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum Compounding implements IdentifiableEnum {
    SIMPLE(1, "SIMPLE", ""), // (1 + rt)
    COMPOUNDED(2, "COMPOUNDED", ""), // (1 + r)^t
    SIMPLE_THEN_COMPOUNDED(3, "SIMPLE-THEN-COMPOUNDED", ""), // SIMPLE if t <= 1 year, else COMPOUNDED
    CONTINUOUS(4, "CONTINUOUS", "");                // exp(rt)

    private final int id;
    private final String code;
    private final String description;

    Compounding(int id, String code, String description) {
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

    public static Compounding fromId(int id) {
        return IdentifiableEnum.fromId(Compounding.class, id);
    }

    public static Compounding fromCode(String code) {
        return IdentifiableEnum.fromCode(Compounding.class, code);
    }
}
