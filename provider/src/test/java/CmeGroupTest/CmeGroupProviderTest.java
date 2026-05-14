/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CmeGroupTest;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.provider.CmeGroup.CmeGroupProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author ep
 */
public class CmeGroupProviderTest {

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        testCme();
    }

    private static void testCme() {
        CmeGroupProvider provider = CmeGroupProvider.getInstance();
/*
        System.out.println("========== TERM SOFR ==========");
        List<Node> nodes = provider.getTermSofrRates();
        if (nodes != null) {
            for (Node n : nodes) {
                System.out.println(n.getSymbol() + "\t" + n.getData().bid());
            }
        }

        System.out.println("");
*/
        System.out.println("========== TERM ESTR ==========");
        LocalDate today = LocalDate.now();
        List<Node> nodes = provider.getTermEsterRates();
        if (nodes != null) {
            for (Node n : nodes) {
                System.out.println(n.getSymbol() + "\t" + n.maturity(today) + "\t" + n.getData().bid());
            }
        }
        
        System.out.println("");
        Node node = provider.getQuote("58@6EM6", Market.FUTURES);
        System.out.println(node.getSymbol() + "\t" + node.getData().bid());
    }
}
