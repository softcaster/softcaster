/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

import java.util.Arrays;

/**
 *
 * @author softc
 */
public enum TxnStatus implements IdentifiableEnum {
    PENDING(1, "PENDING", ""),
    VALIDATING(2, "VALIDATING", ""),
    EXECUTED(3, "EXECUTED", ""),
    SETTLED(4, "SETTLED", ""),
    REJECTED(5, "REJECTED", ""),
    TO_CANCEL(6, "TO_CANCEL", ""),
    CANCELLED(6, "CANCELLED", ""),
    CANCELLED_EXECUTED(7, "CANCELLED_EXECUTED", ""),
    RESTARTING(8, "RESTARTING", ""),
    TO_AMEND(8, "TO_AMEND", ""),
    AMENDED(9, "AMENDED", "");

    private final int id;
    private final String code;
    private final String description;

    TxnStatus(int id, String code, String description) {
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

    public static TxnStatus fromCode(int id) {
        return Arrays.stream(values())
                .filter(e -> e.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingStatus ID: " + id));
    }

    public static TxnStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid AccountingStatus ID: " + code));
    }
}
