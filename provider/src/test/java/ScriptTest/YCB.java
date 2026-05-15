/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ScriptTest;

import org.softcaster.provider.interpreter.YieldCurveBuilder;
import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.bricks.Node;

/**
 *
 * @author ep
 */
public class YCB {

    public static void main(String[] args) {

        FileUtil.initializeLogger();
        FileUtil.initializePython();

        YieldCurveBuilder builder = YieldCurveBuilder.getInstance();
        List<Node> nodes = builder.getNodeList("EUR01");

        if (nodes != null) {
            for (Node n : nodes) {
                System.out.println(n.getSymbol() + "\t" + n.getData().bid());
            }
        } else {
                System.out.println("No nodes!");
            
        }
    }
}
