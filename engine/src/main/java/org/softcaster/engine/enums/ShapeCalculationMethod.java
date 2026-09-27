/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum ShapeCalculationMethod implements IdentifiableEnum {
    AVERAGE(1, "AVERAGE", "Average"),
    MEDIAN(2, "MEDIAN", "Median"),
    WEIGHTED_AVERAGE(3, "WEIGHTED_AVERAGE", "Weighted Average");
   
    private final int id;
    private final String code;
    private final String description;

    ShapeCalculationMethod(int id, String code, String description
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

    public static ShapeCalculationMethod fromId(int id) {
        return IdentifiableEnum.fromId(ShapeCalculationMethod.class, id);
    }

    public static ShapeCalculationMethod fromCode(String code) {
        return IdentifiableEnum.fromCode(ShapeCalculationMethod.class, code);
    }
    
}
