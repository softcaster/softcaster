/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum SbcStatus implements IdentifiableEnum {
    OPEN(1, "OPEN", "Open"),
    CLOSING(2, "CLOSING", "Closing"),
    CLOSED(3, "CLOSED", "Closed"),
    LOCKED(4, "LOCKED", "Loked");

    private final int id;
    private final String code;
    private final String description;

    SbcStatus(int id, String code, String description) {
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

    public static SbcStatus fromId(int id) {
        return IdentifiableEnum.fromId(SbcStatus.class, id);
    }

    public static SbcStatus fromCode(String code) {
        return IdentifiableEnum.fromCode(SbcStatus.class, code);
    }
}
