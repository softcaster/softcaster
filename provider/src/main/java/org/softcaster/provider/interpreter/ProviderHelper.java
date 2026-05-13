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
public class ProviderHelper implements IProviderHelper {

    private IProviderHelper helper;
    private static ProviderHelper instance;

    private ProviderHelper() {
        createHelper();
    }

    private void createHelper() {
        try {
            JythonObjectFactory factory = new JythonObjectFactory(IProviderHelper.class, "provider_helper", "PyProviderHelper");
            helper = (IProviderHelper) factory.createObject();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public static ProviderHelper getInstance() {
        if (instance == null) {
            instance = new ProviderHelper();
        }

        return instance;
    }

    /**
     *
     * @param curveId
     * @return
     */
    @Override
    public List<Node> getNodeList(String curveId) {
        if (helper != null) {
            return helper.getNodeList(curveId);
        } else {
            return null;
        }
    }

    @Override
    public String  getDebugInfo() {
        if (helper != null) {
            return helper.getDebugInfo();
        } else {
            return "";
        }
    }    

}
