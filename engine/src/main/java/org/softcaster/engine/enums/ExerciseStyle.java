/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

public enum ExerciseStyle implements IdentifiableEnum {

    EUROPEAN(1, "EUROPEAN", "Can only be exercised at the expiration date"),
    AMERICAN(2, "AMERICAN", "Can be exercised at any time up to the expiration date"),
    BERMUDAN(3, "BERMUDAN", "Can be exercised on specific dates before expiration");

    private final int id;
    private final String code;
    private final String description;

    ExerciseStyle(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public static ExerciseStyle fromId(int id) {
        return IdentifiableEnum.fromId(ExerciseStyle.class, id);
    }

    public static ExerciseStyle fromCode(String code) {
        return IdentifiableEnum.fromCode(ExerciseStyle.class, code);
    }
}
