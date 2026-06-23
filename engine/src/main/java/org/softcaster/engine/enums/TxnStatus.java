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
    PENDING(1, "PENDING", "Pending"),
    VALIDATING(2, "VALIDATING", "Validating"),
    EXECUTED(3, "EXECUTED", "Executed"),    
    REJECTED(4, "REJECTED", "Rejected"),
    TO_AMEND(5, "TO_AMEND", "To Amend"),
    AMENDED(6, "AMENDED", "Amended"),
    TO_CANCEL(7, "TO_CANCEL", "To Cancel"),
    CANCELLED(8, "CANCELLED", "Cancelled"),
    RESTARTING(9, "RESTARTING", "Restarting"),
    CLAIMED(10, "CLAIMED", "Claimed"); // transazioni "prenotate"

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
