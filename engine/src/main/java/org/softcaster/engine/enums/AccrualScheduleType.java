/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum AccrualScheduleType implements IdentifiableEnum {
    SAS(1,"SAS","Standard Amortization Schedule"), // Piano ammortamento francese a rata costante
    SLP(2,"SLP","Straight Line - Fixed principal repayment"), // Piano ammortamento italiano a quote cap. costanti
    IOL(3,"IOL","Interest only - Full principal at maturity"), // Paino ammortamento Bond
    NONE(100, "NONE", "None");

    private final int id;
    private final String code;
    private final String description;

    AccrualScheduleType(int id, String code, String description) {
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
}
