/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum JournalEntryStatus implements IdentifiableEnum {
    UNCONSOLIDATED(1, "UNCONSOLIDATED", "Unconsolidated"),
    CONSOLIDATED(2, "CONSOLIDATED", "Consolidated"),
    ERROR(3, "ERROR", "Error");

    private final int id;
    private final String code;
    private final String description;

    JournalEntryStatus(int id, String code, String description) {
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

    public static JournalEntryStatus fromId(int id) {
        return IdentifiableEnum.fromId(JournalEntryStatus.class, id);
    }

    public static JournalEntryStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(JournalEntryStatus.class, code);
    }
}
