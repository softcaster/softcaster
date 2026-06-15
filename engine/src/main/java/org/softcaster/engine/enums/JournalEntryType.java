/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum JournalEntryType implements IdentifiableEnum {
    ACCOUNTING(1, "ACCOUNTING", "Accounting"),
    MEMO(2, "MEMO", "Memo"),
    REVERSAL(3, "REVERSAL", "Reversal"),
    ADJUSTMENT(4, "ADJUSTMENT", "Adjustment");

    private final int id;
    private final String code;
    private final String description;

    JournalEntryType(int id, String code, String description) {
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

    public static JournalEntryType fromId(int id) {
        return IdentifiableEnum.fromId(JournalEntryType.class, id);
    }

    public static JournalEntryType fromCode(String code) {
        return IdentifiableEnum.fromCode(JournalEntryType.class, code);
    }

}
