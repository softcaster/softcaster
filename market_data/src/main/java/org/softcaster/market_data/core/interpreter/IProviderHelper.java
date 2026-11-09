/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.market_data.core.interpreter;

import java.util.List;
import org.softcaster.market_data.core.Node;
/**
 *
 * @author softc
 */
public interface IProviderHelper {
    public List<Node>  getNodeList(String symbol);    
    public String  getDebugInfo();    
}
