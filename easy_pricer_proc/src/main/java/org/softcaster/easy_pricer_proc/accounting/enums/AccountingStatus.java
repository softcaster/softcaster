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
public enum AccountingStatus implements IdentifiableEnum {
    NONE(1, "NONE", ""),
    MEMO_BOOKED(2, "MEMO_BOOKED", ""),
    POSTED(3, "POSTED", "");

    private final int id;
    private final String code;
    private final String description;

    AccountingStatus(int id, String code, String description) {
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

    public static AccountingStatus fromCode(int id) {
        return Arrays.stream(values())
                .filter( e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingStatus ID: " + id));
    }

    public static AccountingStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingStatus ID: " + code));
    }
}
