/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.easy_pricer_proc.accounting.enums;

import java.util.Arrays;
import org.softcaster.engine.enums.IdentifiableEnum;

/**
 *
 * @author softc
 */
public enum FinancialStatmentType implements IdentifiableEnum {
    BALANCE_SHEET(1, "BALANCE_SHEET", ""),
    INCOME_STATEMENT(2, "INCOME_STATEMENT", ""),
    OFF_BALANCE_SHEET(3, "OFF_BALANCE_SHEET", "");

    private final int id;
    private final String code;
    private final String description;

    FinancialStatmentType(int id, String code, String description) {
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

    public static FinancialStatmentType fromCode(int id) {
        return Arrays.stream(values())
                .filter( e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid FinancialStatmentType code: " + id));
    }

    public static FinancialStatmentType fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid FinancialStatmentType ID: " + code));
    }
}
