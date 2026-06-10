/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author ep
 */
public enum RollConvention  implements IdentifiableEnum {
    PREVIOUS(1, "PREVIOUS", "Previus"), 
    PREVIOUS_MODIFIED(2, "PREVIOUS-MODIFIED", "Previus Following"),
    FORWARD(3, "FORWARD", "Following"), 
    FORWARD_MODIFIED(4, "FORWARD-MODIFIED", "Modified Following");

    private final int id;
    private final String code;
    private final String description;

    RollConvention(int id, String code, String description) {
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

    public static RollConvention fromId(int id) {
        return IdentifiableEnum.fromId(RollConvention.class, id);
    }

    public static RollConvention fromCode(String code) {
        return IdentifiableEnum.fromCode(RollConvention.class, code);
    }
}
