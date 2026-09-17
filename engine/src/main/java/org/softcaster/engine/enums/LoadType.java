/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

import java.time.LocalDateTime;
import org.softcaster.engine.utils.PeakHoursCalculator;
import org.softcaster.engine.utils.PeakHoursCalculators;

public enum LoadType implements IdentifiableEnum {

    BASE(1, "BASE", "Base load - 24 hours a day, 7 days a week", null),
    PEAK(2, "PEAK", "Peak load - peak hours as defined by market convention", PeakHoursCalculators.EEX_PEAK);

    private final int id;
    private final String code;
    private final String description;
    private final PeakHoursCalculator calculator;

    LoadType(int id, String code, String description, PeakHoursCalculator calculator) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.calculator = calculator;
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

    public boolean isDeliveryHour(LocalDateTime hour, String market) {
        if (calculator == null) {
            return true;
        }
        return calculator.isPeakHour(hour, market);
    }

    public static LoadType fromId(int id) {
        return IdentifiableEnum.fromId(LoadType.class, id);
    }

    public static LoadType fromCode(String code) {
        return IdentifiableEnum.fromCode(LoadType.class, code);
    }
}
