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
        System.out.println("Java Home in uso: " + System.getProperty("java.home"));
        ECBProvider provider = ECBProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("ECB");
        System.out.println(nodes.size());
    }
}
