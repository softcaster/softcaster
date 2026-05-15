/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.interpreter;

import java.util.List;
import org.softcaster.commons.interpreter.JythonObjectFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.Node;

/**
 *
 * @author ep
 */
public class YieldCurveBuilder {

    private IYieldCurveBuilder builder;
    private static YieldCurveBuilder _instance;
    
    private YieldCurveBuilder() {
        createBuilder();
    }

    public static YieldCurveBuilder getInstance() {
        if(_instance == null) {
            _instance = new YieldCurveBuilder();
        }
        
        return _instance;
    }
    
    public List<Node> getNodeList(String curveId) {
        List<Node>  nodes = null;
        if(builder != null) {
            nodes = builder.getNodeList(curveId);
        }
        
        return nodes;
    }

    private void createBuilder() {
        try {
            JythonObjectFactory factory = new JythonObjectFactory(IYieldCurveBuilder.class, "builder", "PyYCB");
            builder = (IYieldCurveBuilder) factory.createObject();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }
}
