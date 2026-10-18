/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.dto;

public sealed interface ForwardResult extends PricingResult
        permits FxForwardResult /*, BondForwardResult */ {
    double forwardRate();
}