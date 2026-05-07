/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

/**
 *
 * @author softc
 */
public enum DAYCOUNT_BASIS {

    NASD_30_360(1, "NASD_30_360", "30 days per month / 360 days per year. Following NASD rules for end-of-month adjustments"),
    ACT_360(2, "ACT_360", "Actual number of days between dates / 360 days per year. Common in money market instruments"),
    ACT_365(3, "ACT_365", "Actual number of days between dates / 365 days per year (Fixed year)"),
    ACT_ACT_ISDA(4, "ACT_ACT_ISDA", "Actual/Actual ISDA - Standard for derivatives"),
    ACT_ACT_ICMA(5, "ACT_ACT_ICMA", "Actual/Actual ICMA - Standard for government bonds"),
    EUR_30_360(6, "EUR_30_360", "30/360 Eurobond\", \"30 days per month / 360 days per year. Following European Bond basis conventions");

    private final int id;
    private final String code;
    private final String description;

    DAYCOUNT_BASIS(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static DAYCOUNT_BASIS fromCode(String text) {
        for (DAYCOUNT_BASIS b : DAYCOUNT_BASIS.values()) {
            if (b.name().equalsIgnoreCase(text) || b.code.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown DayCountBasis: " + text);
    }

    public static DAYCOUNT_BASIS fromId(int id) {
        for (DAYCOUNT_BASIS b : DAYCOUNT_BASIS.values()) {
            if (b.id == id) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unknown DayCountBasis Id: " + id);
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }
}
