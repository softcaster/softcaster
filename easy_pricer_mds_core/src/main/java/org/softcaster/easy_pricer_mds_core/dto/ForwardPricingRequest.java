/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.dto;

import java.time.LocalDate;

/**
 *
 * @author ep
 */
public class ForwardPricingRequest extends PricingRequest {
    
    public double domesticRate = 0.;
    public String domesticRCurve = "";
    public double foreignRate = 0.;
    public String foreignRCurve = "";
    public LocalDate maturityDate;
}
