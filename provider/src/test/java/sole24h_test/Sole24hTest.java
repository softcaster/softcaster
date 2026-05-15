/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sole24h_test;

import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.sole24h.Sole24hProvider;

/**
 *
 * @author ep
 */
public class Sole24hTest {
    
    private static void testIrsYieldCurves() {
        Sole24hProvider provider = Sole24hProvider.getInstance();

        List<Node> nodes = provider.getIrsYieldCurve();
        for(Node n: nodes) {
            System.out.println(n.getSymbol() + "\t" + n.getData().bid());
        }
    }

    public static void main(String[] args) {
        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        System.out.println("########## IRS Yield Curve ##########");
        testIrsYieldCurves();
    }
}
