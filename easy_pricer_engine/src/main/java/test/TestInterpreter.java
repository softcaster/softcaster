/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.python.util.PythonInterpreter;
import org.softcaster.commons.xml.ParamsMgr;
import ph.alephzero.finance.interpreter.CustomInput;
import ph.alephzero.finance.interpreter.Engine;

/**
 *
 * @author ep
 */
public class TestInterpreter {

    public static void main(String[] args) throws IOException {
        File conf = new File(System.getProperty("user.dir")
                + "//conf//log4j.conf");
        System.setProperty("log4j.configuration", "file:" + conf);

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
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

        Engine engine = new Engine();
        CustomInput input = new CustomInput();
        input.value = 80.;
        input.desc = "strike";
        List<CustomInput> inputs = new ArrayList<>();
        inputs.add(input);
        double fv = engine.fairValue(inputs);
        System.out.println(fv);

    }

}
