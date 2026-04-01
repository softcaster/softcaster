/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.softcaster.easy_import.xml.BondLoaderMgr;
import org.softcaster.easy_import.xml.ItemBond;

/**
 *
 * @author softc
 */
public class BondImportMgrHelper {
    
    protected BondLoaderMgr loader = null;

    protected List<String> getIsinList() throws FileNotFoundException, XMLStreamException {
        if (loader != null) {
            return loader.getIsinList();
        } else {
            return null;
        }
    }

    protected ItemBond getBondDataFromXML(String isin) throws IOException, FileNotFoundException, XMLStreamException {

        if (loader != null) {
            return loader.getBondByIsin(isin);
        } else {
            return null;
        }
    }
   
}
