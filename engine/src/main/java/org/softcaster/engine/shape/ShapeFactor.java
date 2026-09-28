/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.shape;

import java.time.DayOfWeek;

public record ShapeFactor(
        int month,
        DayOfWeek dayOfWeek,
        double factor) {

}
