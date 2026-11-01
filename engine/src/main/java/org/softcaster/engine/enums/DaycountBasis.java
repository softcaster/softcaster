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

    NASD_30_360(1, "NASD_30_360", "30/360 US (NASD)", DayCountCalculators.NASD_30_360), // 30 giorni al mese / 360, fine mese secondo le regole NASD
    ACT_360(2, "ACT_360", "ACT/360", DayCountCalculators.ACT_360), // giorni effettivi / 360, mercato monetario
    ACT_365(3, "ACT_365", "ACT/365 Fixed", DayCountCalculators.ACT_365), // giorni effettivi / 365 (anno fisso)
    ACT_ACT_ISDA(4, "ACT_ACT_ISDA", "ACT/ACT ISDA", DayCountCalculators.ACT_ACT_ISDA), // standard dei derivati
    ACT_ACT_ICMA(5, "ACT_ACT_ICMA", "ACT/ACT ICMA", DayCountCalculators.ACT_ACT_ICMA), // standard dei titoli di stato
    EUR_30_360(6, "EUR_30_360", "30/360 European", DayCountCalculators.EUR_30_360); // 30 giorni al mese / 360, convenzione Eurobond

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
