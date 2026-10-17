/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.dto;

public sealed interface CashFlowInput extends PricingInput
        permits XRBInput, FRBInput /*, DepositInput, LoanInput */ {

    CashFlowSchedule schedule();
}
