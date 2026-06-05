/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.core.data.enums;

/**
 *
 * @author ep
 */
public enum TxnComponentType {
    BROKER_FEE(1, "BROKER_FEE", "Broker Transaction and Execution Fees"),
    EXCHANGE_FEE(2, "EXCHANGE_FEE", "CME / Clearing House Regulatory Fees"),
    INITIAL_MARGIN(3, "INITIAL_MARGIN", "Initial Margin Deposit Requirement"),
    MAINTENANCE_MARGIN(4, "MAINTENANCE_MARGIN", "Maintenance Margin Requirement"),
    OPTION_PREMIUM(5, "OPTION_PREMIUM", "Option Premium Paid or Written");

    private final int id;
    private final String code;
    private final String description;

    TxnComponentType(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    public static TxnComponentType fromId(int id) {
        for (TxnComponentType item : TxnComponentType.values()) {
            if (item.getId() == id) {
                return item;
            }
        }
        throw new IllegalArgumentException("Invalid TxnComponentType ID: " + id);
    }

    public static TxnComponentType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }

        // Scorre tutti i valori dell'enum
        for (TxnComponentType item : TxnComponentType.values()) {
            // Usa equalsIgnoreCase se vuoi ignorare maiuscole/minuscole, altrimenti usa .equals()
            if (item.getCode().equals(code)) {
                return item;
            }
        }

        // Lancia un'eccezione se il codice non corrisponde a nessun elemento
        throw new IllegalArgumentException("Invalid TxnComponentType code: " + code);
    }
}
