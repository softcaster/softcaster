/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_shared.utils;

import java.time.LocalDate;
import org.softcaster.easy_pricer_shared.enums.Frequency;

@FunctionalInterface
public interface DayCountCalculator {
    double calculate(LocalDate start, LocalDate end, Frequency frequency);

    default double getTime() { // Metodo default
        return 365;
    }
}
