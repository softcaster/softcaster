/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.accounting;

import org.softcaster.engine.enums.IdentifiableEnum;

/**
 *
 * @author ep
 */
public enum AccountingEvent implements IdentifiableEnum {
    TRADE_BOOKED(1, "TRADE_BOOKED", ""),
    SETTLED(1, "SETTLED", ""),
    MTM(1, "MTM", ""),
    ACCRUAL(1, "ACCRUAL", "");

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

    public static AccountingEvent fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }

        // Scorre tutti i valori dell'enum
        for (AccountingEvent ae : AccountingEvent.values()) {
            // Usa equalsIgnoreCase se vuoi ignorare maiuscole/minuscole, altrimenti usa .equals()
            if (ae.getCode().equals(code)) {
                return ae;
            }
        }

        // Lancia un'eccezione se il codice non corrisponde a nessun elemento
        throw new IllegalArgumentException("Invalid AccountingEvent code: " + code);
    }

    public static AccountingEvent fromId(int id) {
        for (AccountingEvent ae : AccountingEvent.values()) {
            if (ae.getId() == id) {
                return ae;
            }
        }
        throw new IllegalArgumentException("Invalid AccountingEvent ID: " + id);
    }

}
