/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.utils;

import java.time.LocalDateTime;

public interface PeakHoursCalculator {

    boolean isPeakHour(LocalDateTime hour, String market);
}
