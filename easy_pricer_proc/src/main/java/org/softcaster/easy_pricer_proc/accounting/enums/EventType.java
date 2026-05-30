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
public enum EventType implements IdentifiableEnum {
    TRADE_EXECUTION(1, "TRADE_EXECUTION", ""),
    TRADE_CANCEL(2, "TRADE_CANCEL", ""),
    MTM(3, "MTM", ""),
    COUPON(4, "COUPON", ""),
    ACCRUAL(5, "COUPON", ""),
    SETTLEMENT(6, "SETTLEMENT", ""),
    MATURITY(7, "MATURITY", ""),
    FX_REVALUATION(8, "FX_REVALUATION", "");

    private final int id;
    private final String code;
    private final String description;

    EventType(int id, String code, String description) {
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

    public static EventType fromCode(int id) {
        return Arrays.stream(values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid EventType code: " + id));
    }

    public static EventType fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid EventType ID: " + code));
    }

}
