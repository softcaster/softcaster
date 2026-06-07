/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum EventSourceType implements IdentifiableEnum {
    TRADE(1, "TRADE", "Trade"),
    INSTRUMENT(2, "INSTRUMENT", "Instrument"),
    POSITION_DETAIL(3, "POSITION_DETAIL", "Position");   

    private final int id;
    private final String code;
    private final String description;

    EventSourceType(int id, String code, String description) {
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
    
    public static EventSourceType fromId(int id) {
        return IdentifiableEnum.fromId(EventSourceType.class, id);
    }

    public static EventSourceType fromCode(String code) {
        return IdentifiableEnum.fromCode(EventSourceType.class, code);
    }
    
}
