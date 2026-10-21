/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BlueGammaTest;

import java.util.List;
import org.softcaster.provider.bluegamma.BlueGammaOisProvider;
import org.softcaster.provider.bricks.Node;

/**
 *
 * @author ep
 */
public class BlueGammaOisProviderTest {
    
    public static void main(String[] args) {
        BlueGammaOisProvider provider = BlueGammaOisProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("EUR_OIS");
        for(Node node: nodes) {
            System.out.println("Ric: " + node.getSymbol() + "\t" + "Rate: " + node.getData().bid());
        }
        
    }
}
