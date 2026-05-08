/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum Form implements IdentifiableEnum {
    BEARER(1, "BEARER", "Bearer"),
    REGISTERED(2, "REGISTERED", "Registered"),
    BOOK_ENTRY_BOND(3, "BOOK-ENTRY-BOND", "Book-entry Bond"),
    NONE(100, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    Form(int id, String code, String description) {
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
}
