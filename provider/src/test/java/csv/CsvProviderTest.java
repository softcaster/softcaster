/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package csv;

import java.io.IOException;
import java.util.List;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.csv.CsvProvider;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author softc
 */
public class CsvProviderTest {

    public static void main(String[] args) {
        testCsvProvider();
    }

    private static void testCsvProvider() {
        CsvProvider provider = CsvProvider.getInstance();
        List<Node> nodes = provider.getYieldCurveNodes("ITAYIELD");
        for (Node node : nodes) {
            System.out.println(node.getSymbol() + "\t" + node.getData().bid());
        }
    }
}
