/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

public sealed interface PricingResult permits CashFlowResult,ForwardResult /*, FxResult, OptionResult */ {
    String instrumentId();
    LocalDate valuationDate();
}
