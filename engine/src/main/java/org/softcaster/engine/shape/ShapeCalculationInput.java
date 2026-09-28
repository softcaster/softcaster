/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.shape;
    
import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.enums.ShapeGranularity;

public record ShapeCalculationInput(
        List<SpotObservation> observations,                                             
        ShapeGranularity granularity,
        LocalDate observationFrom,
        LocalDate observationTo
        ) {

}
