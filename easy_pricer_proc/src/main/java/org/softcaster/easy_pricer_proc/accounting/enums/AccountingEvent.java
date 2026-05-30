/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.accounting.enums;

import java.util.Arrays;
import org.softcaster.engine.enums.IdentifiableEnum;

/**
 *
 * @author ep
 */
public enum AccountingEvent implements IdentifiableEnum {
    TRADE_BOOKED(1, "TRADE_BOOKED", ""),
    SETTLED(2, "SETTLED", ""),
    MTM(3, "MTM", ""),
    ACCRUAL(4, "ACCRUAL", "");

    private final int id;
    private final String code;
    private final String description;

    AccountingEvent(int id, String code, String description) {
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
    public static AccountingEvent fromCode(int id) {
        return Arrays.stream(values())
                .filter( e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingEvent code: " + id));
    }

    public static AccountingEvent fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingEvent ID: " + code));
    }
}
