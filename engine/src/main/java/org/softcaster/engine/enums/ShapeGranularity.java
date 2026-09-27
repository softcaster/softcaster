/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum ShapeGranularity implements IdentifiableEnum {
    WEEKLY(1, "WEEKLY", "Weekly profile"),
    MONTHLY(2, "MONTHLY", "Monthly profile"),
    MONTH_DOW(3, "MONTH_DOW", "Monthly profile differentiated by day of week"),
    MONTH_DOW_HOUR(4, "MONTH_DOW_HOUR", "Monthly profile differentiated by day of week and hour");
    
    private final int id;
    private final String code;
    private final String description;

    ShapeGranularity(int id, String code, String description
    ) {
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

    public static ShapeGranularity fromId(int id) {
        return IdentifiableEnum.fromId(ShapeGranularity.class, id);
    }

    public static ShapeGranularity fromCode(String code) {
        return IdentifiableEnum.fromCode(ShapeGranularity.class, code);
    }
}
