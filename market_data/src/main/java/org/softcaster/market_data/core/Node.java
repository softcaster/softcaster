package org.softcaster.market_data.core;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;

/**
 *
 * @author softc
 */
public record Node(String symbol,
        Offset offset,
        DaycountBasis daycount,
        Compounding compounding,
        CurveNodeType nodeType,
        Data data) {

}
