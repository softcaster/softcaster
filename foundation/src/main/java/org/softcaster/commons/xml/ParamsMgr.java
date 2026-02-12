/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xml;

import java.util.List;

/**
 *
 * @author ep
 */
public class ParamsMgr {

    private List<Item> items = null;
    private static ParamsMgr _instance = null;

    private ParamsMgr() {

        Params parser = Params.getInstance();
        if (parser != null) {
            items = parser.readConfig(System.getProperty("user.dir") + "//conf//params.xml");
        }
    }

    private String getValue(String Id) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        for (Item p : items) {
            if (p.getId().equals(Id)) {
                return p.getValue();
            }
        }

        return "";
    }

    public String getParamValue(String paramName) {
        if (_instance != null) {
            return _instance.getValue(paramName);
        } else {
            return "";
        }
    }

    public static ParamsMgr getInstance() {
        if (_instance == null) {
            _instance = new ParamsMgr();
        }
        return _instance;
    }

}
