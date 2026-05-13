/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.provider.interpreter;

import java.util.List;
import org.softcaster.provider.bricks.Node;
/**
 *
 * @author softc
 */
public interface IProviderHelper {
    public List<Node>  getNodeList(String symbol);    
    public String  getDebugInfo();    
}
