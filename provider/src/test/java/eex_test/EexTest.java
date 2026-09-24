/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eex_test;

import org.softcaster.provider.eex.EexProvider;
import static org.softcaster.provider.enums.Market.FUTURES;

/**
 *
 * @author ep
 */
public class EexTest {

    public static void main(String[] args) {
        testEex();
    }

    private static void testEex() {
        String productKeyDE = "DEBY@DE@Base@POWER@F@202701";
        String productKeyDEQ = "DEBQ@DE@Base@POWER@F@202610";
        String productKeyIT = "FDBM@IT@Base@POWER@F@202701";
        String productKeyTHE = "THEDA@THE@Base@NATGAS@S@null";

        
        EexProvider provider = EexProvider.getInstance();
        
        double value = provider.getMktQuote(productKeyDE, FUTURES).getData().bid();
        System.out.println(value);

        value = provider.getMktQuote(productKeyDEQ, FUTURES).getData().bid();
        System.out.println(value);

        value = provider.getMktQuote(productKeyIT, FUTURES).getData().bid();
        System.out.println(value);

        value = provider.getMktQuote(productKeyTHE, FUTURES).getData().bid();
        System.out.println(value);
    }
}
