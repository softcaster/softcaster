/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;

@FunctionalInterface
public interface DayCountCalculator {
    double calculate(LocalDate start, LocalDate end);
}
