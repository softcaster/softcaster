/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.easy_pricer_shared.enums;

import java.time.LocalDate;

/**
 *
 * @author ep
 */
public enum Frequency implements IdentifiableEnum {
    ANNUAL(1, "ANNUAL", "Annual", 1, 12),
    SEMI_ANNUAL(2, "SEMI-ANNUAL", "Semi Annual", 2, 6),
    E4M(3, "E4M", "E4M','Every 4 months", 3, 4),
    QUARTERLY(4, "QUARTERLY", "Quarterly", 4, 3),
    BI_MONTHLY(5, "BI-MONTHLY", "Every two months", 6, 2),
    MONTHLY(6, "MONTHLY", "Monthly", 12, 1),
    CUSTOM(7, "CUSTOM", "Custom", 0, 0),
    NONE(100, "NONE", "At maturity only", 0, 0); // Es ZC Bond

    private final int id;
    private final String code;
    private final String description;
    private final int yearFraction;
    private final int months;

    Frequency(int id, String code, String description, int yearFraction, int months) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.yearFraction = yearFraction;
        this.months = months;
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

    public LocalDate offset(LocalDate date) {
        return date.plusMonths(months);
    }

    public LocalDate backwardOffset(LocalDate date) {
        return date.minusMonths(months);
    }

    public static Frequency fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }

        // Scorre tutti i valori dell'enum
        for (Frequency frequency : Frequency.values()) {
            // Usa equalsIgnoreCase se vuoi ignorare maiuscole/minuscole, altrimenti usa .equals()
            if (frequency.getCode().equals(code)) {
                return frequency;
            }
        }

        // Lancia un'eccezione se il codice non corrisponde a nessun elemento
        throw new IllegalArgumentException("Invalid Frequency code: " + code);
    }
}
