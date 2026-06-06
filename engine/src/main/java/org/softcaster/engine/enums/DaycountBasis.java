/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

import java.time.LocalDate;
import org.softcaster.engine.utils.DayCountCalculator;
import org.softcaster.engine.utils.DayCountCalculators;

/**
 *
 * @author softc
 */
public enum DaycountBasis implements IdentifiableEnum {

    NASD_30_360(1, "NASD_30_360", "30 days per month / 360 days per year. Following NASD rules for end-of-month adjustments", DayCountCalculators.NASD_30_360),
    ACT_360(2, "ACT_360", "Actual number of days between dates / 360 days per year. Common in money market instruments", DayCountCalculators.ACT_360),
    ACT_365(3, "ACT_365", "Actual number of days between dates / 365 days per year (Fixed year)", DayCountCalculators.ACT_365),
    ACT_ACT_ISDA(4, "ACT_ACT_ISDA", "Actual/Actual ISDA - Standard for derivatives", null),
    ACT_ACT_ICMA(5, "ACT_ACT_ICMA", "Actual/Actual ICMA - Standard for government bonds", DayCountCalculators.ACT_ACT_ICMA),
    EUR_30_360(6, "EUR_30_360", "30/360 Eurobond\", \"30 days per month / 360 days per year. Following European Bond basis conventions", null);

    private final int id;
    private final String code;
    private final String description;
    private final DayCountCalculator calculator;

    DaycountBasis(int id, String code, String description, DayCountCalculator calculator) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.calculator = calculator;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCode() {
        return code;
    }

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    public double getTime() {
        return switch (this) {
            case NASD_30_360, ACT_360, EUR_30_360 ->
                360.;
            default ->
                (calculator != null) ? calculator.getTime() : 365.;
        };
    }

    public double calculate(LocalDate accrualStart, LocalDate accrualEnd, Frequency freq) {
        if (calculator != null) {
            return calculator.calculate(accrualStart, accrualEnd, freq);
        } else {
            return 0;
        }
    }

    public static DaycountBasis fromId(int id) {
        return IdentifiableEnum.fromId(DaycountBasis.class, id);
    }

    public static DaycountBasis fromCode(String code) {
        return IdentifiableEnum.fromCode(DaycountBasis.class, code);
    }
}
