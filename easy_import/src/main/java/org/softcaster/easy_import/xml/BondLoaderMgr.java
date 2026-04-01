/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.xml;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author ep
 */
public class BondLoaderMgr {

    private List<ItemBond> items = null;
    private static BondLoaderMgr _instance = null;

    private BondLoaderMgr() throws FileNotFoundException, XMLStreamException {

        BondStaxParser parser = BondStaxParser.getInstance();
        if (parser != null) {
            ParamsMgr paramsMgr = ParamsMgr.getInstance();
            String fileName = paramsMgr.getParamValue("XML_FILENAME");
            //items = parser.readFile(System.getProperty("user.dir") + "//import//bonds_ita.xml");
            items = parser.readFile(System.getProperty("user.dir") + "//import//"+ fileName);
        }
    }

    private ItemBond getBond(String isin) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        for (ItemBond p : items) {
            if (p.isincode.equals(isin)) {
                return p;
            }
        }

        return null;
    }

    public ItemBond getBondByIsin(String isin) {
        if (_instance != null) {
            return _instance.getBond(isin);
        } else {
            return null;
        }
    }

    public List<String> getIsinList() {
        if (items == null || items.isEmpty()) {
            return null;
        } else {
            List<String> isinList = new ArrayList<>();
            for (ItemBond item : items) {
                isinList.add(item.isincode);
            }

            return isinList;
        }
    }

    public static BondLoaderMgr getInstance() throws FileNotFoundException, XMLStreamException {
        if (_instance == null) {
            _instance = new BondLoaderMgr();
        }
        return _instance;
    }
}
