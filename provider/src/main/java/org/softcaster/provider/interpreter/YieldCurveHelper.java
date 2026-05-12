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
 * @author softc
 */
public class YieldCurveHelper {

    private IYieldCurveHelper helper;

    public YieldCurveHelper() {
        createHelper();
    }

    private void createHelper() {
        try {
            JythonObjectFactory factory = new JythonObjectFactory(IYieldCurveHelper.class, "helper", "PyYcHelper");
            helper = (IYieldCurveHelper) factory.createObject();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public List<Node> getNodeList(String curveId) {
        if (helper != null) {
            return helper.getNodeList(curveId);
        } else {
            return null;
        }
    }
}
