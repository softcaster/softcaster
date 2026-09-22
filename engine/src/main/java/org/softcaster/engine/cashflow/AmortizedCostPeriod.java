/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;

public record AmortizedCostPeriod(
    LocalDate accrualStart,
    LocalDate accrualEnd,
    double openingCarryingValue,
    double effectiveInterest,     // IRR_periodale x openingCarryingValue
    double couponCashInterest,    // dal CashFlow contrattuale (gia' calcolato da BulletAmortizationStrategy)
    double discountAccretion,     // effectiveInterest - couponCashInterest
    double closingCarryingValue   // opening + accretion
) {}