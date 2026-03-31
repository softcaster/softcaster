/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider;

import java.io.File;
import java.util.Properties;
import org.python.util.PythonInterpreter;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author softc
 */
public class MarketDataProviderHelper {

    public static void initializeLogger() {

        // Impostazioni log file
        File conf = new File(System.getProperty("user.dir")
                + "//conf//log4j.conf");
        System.setProperty("log4j.configuration", "file:" + conf);
    }

    public static void initializePython() {

        String scriptsPath = System.getProperty("user.dir") + "\\scripts";
        String fullPythonPath = "__pyclasspath__/Lib" + File.pathSeparator + scriptsPath;

        Properties props = new Properties();
        props.put("python.console.encoding", "UTF-8");
        props.put("python.import.site", "false"); // Disabilita la ricerca di siti esterni
        props.setProperty("python.path", fullPythonPath);
        // Leggo parametro che attiva debug python
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String debugMode = paramsMgr.getParamValue("PY_DEBUG");
        props.put("python.debug", debugMode);
        PythonInterpreter.initialize(System.getProperties(), props, new String[]{""});

    }
}
