/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.marketdataprovider.interpreter;

import java.util.List;
import org.softcaster.marketdataprovider.YieldNode;

/**
 *
 * @author softc
 */
public interface IYieldCurveHelper {
    public List<YieldNode>  getNodeList(String curveId);
}
