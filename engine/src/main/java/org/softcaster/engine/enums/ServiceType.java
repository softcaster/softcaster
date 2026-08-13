/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum ServiceType implements IdentifiableEnum {
    TSRV(1, "TSRV", "Trade Capture Service"),
    PSRV(2, "PSRV", "Trade Processing Service"),
    MSRV(3, "MSRV", "Mtm Service"),   
    ASRV(4, "ASRV", "Accounting Service"),   
    LSRV(5, "LSRV", "Life Cycle Service");

    private final int id;
    private final String code;
    private final String description;

    ServiceType(int id, String code, String description) {
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
    
    public static ServiceType fromId(int id) {
        return IdentifiableEnum.fromId(ServiceType.class, id);
    }

    public static ServiceType fromCode(String code) {
        return IdentifiableEnum.fromCode(ServiceType.class, code);
    }    
}
