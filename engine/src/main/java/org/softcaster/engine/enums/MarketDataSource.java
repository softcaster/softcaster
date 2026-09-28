/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum MarketDataSource  implements IdentifiableEnum {
    EEX(1, "EEX", "Eex"), 
    ENTSO_E(2, "ENTSO_E", "Entso E"), 
    VENDOR(3, "VENDOR", "Vendor"), 
    INTERNAL(4, "INTERNAL", "Internal"),
    GME(5, "GME", "Gestore Mercati Energetici"); 
    private final int id;
    private final String code;
    private final String description;

    MarketDataSource(int id, String code, String description) {
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

    public static MarketDataSource fromId(int id) {
        return IdentifiableEnum.fromId(MarketDataSource.class, id);
    }

    public static MarketDataSource fromCode(String code) {
        return IdentifiableEnum.fromCode(MarketDataSource.class, code);
    }
    
}
