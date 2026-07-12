/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum AccountingEventStatus implements IdentifiableEnum {
    NEW(1, "PENDING", "New"),
    PROCESSING(2, "PROCESSING", "Processing"),
    PROCESSED(3, "PROCESSED", "Processed"),
    FAILED(4, "FAILED", "Failed");

    private final int id;
    private final String code;
    private final String description;

    AccountingEventStatus(int id, String code, String description) {
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

    public static AccountingEventStatus fromId(int id) {
        return IdentifiableEnum.fromId(AccountingEventStatus.class, id);
    }

    public static AccountingEventStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(AccountingEventStatus.class, code);
    }

}
