/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

import java.time.LocalDate;
import java.time.Period;

/**
 * Frequenze di pagamento. Le frequenze mensili (months > 0) valgono per le
 * cedole; settimanale e giornaliera solo per i regolamenti periodici (es.
 * accumulator): i generatori di cash flow le rifiutano.
 *
 * @author ep
 */
public enum Frequency implements IdentifiableEnum {
    ANNUAL(1, "ANNUAL", "Annual", 1, Period.ofMonths(12)),
    SEMI_ANNUAL(2, "SEMI-ANNUAL", "Semi-annual", 2, Period.ofMonths(6)),
    E4M(3, "E4M", "Every 4 months", 3, Period.ofMonths(4)),
    QUARTERLY(4, "QUARTERLY", "Quarterly", 4, Period.ofMonths(3)),
    BI_MONTHLY(5, "BI-MONTHLY", "Bi-monthly", 6, Period.ofMonths(2)),
    MONTHLY(6, "MONTHLY", "Monthly", 12, Period.ofMonths(1)),
    CUSTOM(7, "CUSTOM", "Custom", 0, Period.ZERO),
    WEEKLY(8, "WEEKLY", "Weekly", 52, Period.ofWeeks(1)),
    DAILY(9, "DAILY", "Daily", 365, Period.ofDays(1)),
    NONE(100, "NONE", "At maturity only", 0, Period.ZERO); // Es ZC Bond

    private final int id;
    private final String code;
    private final String description;
    private final int yearFraction;
    private final Period period;

    Frequency(int id, String code, String description, int yearFraction, Period period) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.yearFraction = yearFraction;
        this.period = period;
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

    /**
     * @return the yearFraction
     */
    public int getYearFraction() {
        return yearFraction;
    }

    /**
     * @return true per le frequenze mensili, le sole ammesse per le cedole
     */
    public boolean isCouponFrequency() {
        return period.getMonths() > 0 || period.getYears() > 0;
    }

    public LocalDate offset(LocalDate date) {
        return date.plus(period);
    }

    public LocalDate backwardOffset(LocalDate date) {
        return date.minus(period);
    }

    public static Frequency fromId(int id) {
        return IdentifiableEnum.fromId(Frequency.class, id);
    }

    public static Frequency fromCode(String code) {
        return IdentifiableEnum.fromCode(Frequency.class, code);
    }
}
