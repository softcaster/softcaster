/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.engine.enums;

/**
 *
 * @author svil
 */
public enum OffsetType implements IdentifiableEnum {
    DAYS(1, "DAYS", "Days"),
    MONTHS(2, "MONTHS", "Months"),
    YEARS(3, "YEARS", "Years"),
    NONE(4, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    OffsetType(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static OffsetType fromId(int id) {

        // Scorre tutti i valori dell'enum
        for (OffsetType offsetType : OffsetType.values()) {
            // Usa equalsIgnoreCase se vuoi ignorare maiuscole/minuscole, altrimenti usa .equals()
            if (offsetType.getId() == id) {
                return offsetType;
            }
        }

        // Lancia un'eccezione se l'id non corrisponde a nessun elemento
        throw new IllegalArgumentException("Invalid OffsetType id: " + id);
    }

    public static OffsetType fromOrdinal(int ordinal) {
        // Recupera l'array di tutti i valori dell'enum
        OffsetType[] values = OffsetType.values();

        // Verifica che l'indice sia valido usando la proprietà .length corretta
        if (ordinal < 0 || ordinal >= values.length) {
            throw new IndexOutOfBoundsException("Invalid ordinal: " + ordinal);
        }

        // Restituisce l'elemento all'indice specificato
        return values[ordinal];
    }
    public static OffsetType fromCode(String code) {
       if (code == null) {
            throw new IllegalArgumentException("Code cannot be null");
        }

        // Scorre tutti i valori dell'enum
        for (OffsetType offsetType : OffsetType.values()) {
            // Usa equalsIgnoreCase se vuoi ignorare maiuscole/minuscole, altrimenti usa .equals()
            if (offsetType.getCode().equals(code)) {
                return offsetType;
            }
        }

        // Lancia un'eccezione se il codice non corrisponde a nessun elemento
        throw new IllegalArgumentException("Invalid DaycountBasis code: " + code);
    }
}
