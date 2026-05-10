/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import org.softcaster.engine.enums.OptionStyle;
import org.softcaster.engine.enums.OptionType;

public record OptionData(
        double strike,
        double volatility,
        OptionStyle style,
        OptionType type
        ) {

}
