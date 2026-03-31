/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.interpreter;

import java.util.List;
import org.softcaster.commons.interpreter.JythonObjectFactory;
import org.softcaster.commons.utils.LoggerMgr;

/**
 *
 * @author ep
 */
public class Engine {
    
    private IEngine engine;
    
    public Engine() {
        createEngine();
    }

    private void createEngine() {
        try {
            JythonObjectFactory factory = new JythonObjectFactory(IEngine.class, "engine", "PyEngine");
            engine = (IEngine) factory.createObject();
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    public double fairValue(List<CustomInput> inputs) {
        if (engine != null) {
            return engine.fairValue(inputs);
        } else {
            return 0.;
        }
        
    }
}
