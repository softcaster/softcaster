/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ECBTest;

import java.util.List;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.ecb.ECBProvider;

/**
 *
 * @author ep
 */
public class ECBTest {

    public static void main(String[] args) {
        testECB();
    }

    private static void testECB() {
        System.out.println("java.home = " + System.getProperty("java.home"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        ECBProvider provider = ECBProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("ECB");
        System.out.println(nodes.size());
    }
}
