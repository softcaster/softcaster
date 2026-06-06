/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum TxnStatus implements IdentifiableEnum {
    PENDING(1, "PENDING", ""),
    VALIDATING(2, "VALIDATING", ""),
    EXECUTED(3, "EXECUTED", ""),
    REJECTED(4, "REJECTED", ""),
    TO_AMEND(5, "TO_AMEND", ""),
    AMENDED(6, "AMENDED", ""),
    TO_CANCEL(7, "TO_CANCEL", ""),
    CANCELLED(8, "CANCELLED", ""),
    RESTARTING(9, "RESTARTING", "");

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
    
    public static TxnStatus fromId(int id) {
        return IdentifiableEnum.fromId(TxnStatus.class, id);
    }

    public static TxnStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(TxnStatus.class, code);
    }
}
