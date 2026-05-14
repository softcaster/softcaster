/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;

public record CurveNodeInput(
    Offset tenorOffset, 
    double rate, 
    DaycountBasis daycount, 
    Compounding compounding
) {}

