/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum Frequency implements IdentifiableEnum {
    ANNUAL(1, "ANNUAL", "Annual", 1),
    SEMI_ANNUAL(2, "SEMI-ANNUAL", "Semi Annual", 2),
    E4M(3, "E4M", "E4M','Every 4 months", 3),
    QUARTERLY(4, "QUARTERLY", "Quarterly", 4),
    BI_MONTHLY(5, "BI-MONTHLY", "Every two months", 6),
    MONTHLY(6, "MONTHLY", "Monthly", 12),
    CUSTOM(7, "CUSTOM", "Custom", 0),
    NONE(100, "NONE", "None", 0);

    private final int id;
    private final String code;
    private final String description;
    private final int yearFraction;

    Frequency(int id, String code, String description, int yearFraction) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.yearFraction = yearFraction;
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

    /**
     * @return the yearFraction
     */
    public int getYearFraction() {
        return yearFraction;
    }
}
