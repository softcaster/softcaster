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
public enum AccountNature implements IdentifiableEnum {
    ASSET(1, "ASSET", ""),
    LIABILITY(2, "LIABILITY", ""),
    EQUITY(3, "EQUITY", ""),
    INCOME(3, "INCOME", ""),
    EXPENSE(3, "EXPENSE", ""),
    MEMORANDUM(3, "MEMORANDUM", "");

    private final int id;
    private final String code;
    private final String description;

    AccountNature(int id, String code, String description) {
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

    public static AccountNature fromCode(int id) {
        return Arrays.stream(values())
                .filter( e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountNature code: " + id));
    }

    public static AccountNature fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountNature ID: " + code));
    }

}
