/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.interpreter;

import java.util.List;
import org.softcaster.commons.interpreter.JythonObjectFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.YieldNode;

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

    @Override
    public List<YieldNode> getNodeList(String curveId) {
        if (helper != null) {
            return helper.getNodeList(curveId);
        } else {
            return null;
        }
    }

}
