/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum Tenor implements IdentifiableEnum {

    ON(1, "ON", "Overnight"),
    TN(2, "TN", "Tomorrow/Next"),
    W1(3, "1W", "1 Week"),
    M1(4, "1M", "1 Month"),
    M3(5, "3M", "3 Months"),
    M6(6, "6M", "6 Months"),
    M8(7, "9M", "9 Months"),
    M12(8, "12M", "12 Months");
    
    private final int id;
    private final String code;
    private final String description;

    Tenor(int id, String code, String description
    ) {
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

    public static Tenor fromId(int id) {
        return IdentifiableEnum.fromId(Tenor.class, id);
    }

    public static Tenor fromCode(String code) {
        return IdentifiableEnum.fromCode(Tenor.class, code);
    }
}
