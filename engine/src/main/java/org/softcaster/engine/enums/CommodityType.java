/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum CommodityType implements IdentifiableEnum {

    CRUDE_OIL(1, "CRUDE_OIL", "Crude Oil"), 
    POWER(2, "POWER", "Power"), // Energia elettrica
    NATGAS(3, "NATGAS", "Natural Gas"),
    GOLD(4, "GOLD", "Gold"),
    SILVER(5, "SILVER", "Silver"),
    PLATINUM(6, "PLATINUM", "Platinum"),
    PALLADIUM(7, "PALLADIUM", "Palladium");

    private final int id;
    private final String code;
    private final String description;
   
    CommodityType(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    
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

    public static CommodityType fromId(int id) {
        return IdentifiableEnum.fromId(CommodityType.class, id);
    }

    public static CommodityType fromCode(String code) {
        return IdentifiableEnum.fromCode(CommodityType.class, code);
    }
}