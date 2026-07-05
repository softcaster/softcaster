/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum AccountingPhase implements IdentifiableEnum {
    NONE(1, "NONE", "Not accounted"),
    MEMO_POSTED(2, "MEMO_POSTED", "Memo accounting posted"),
    OFFICIAL_POSTED(3, "OFFICIAL_POSTED", "Official accounting posted");

    private final int id;
    private final String code;
    private final String description;

    AccountingPhase(int id, String code, String description) {
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
    
    public static AccountingPhase fromId(int id) {
        return IdentifiableEnum.fromId(AccountingPhase.class, id);
    }

    public static AccountingPhase fromCode(String code) {
        return IdentifiableEnum.fromCode(AccountingPhase.class, code);
    }
}    
