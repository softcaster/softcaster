/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.shape;

import java.util.List;

public record ShapeCalculationResult(
        List<ShapeFactor> factors,
        double normalizationCheck
        ) {

}
