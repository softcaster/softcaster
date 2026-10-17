/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import org.softcaster.engine.enums.CouponProjectionMethod;

public record FloatingCouponSpec(double referenceRate, double spread, CouponProjectionMethod projectionMethod,
        String projectionCurve, String projectionIndex) {

}
