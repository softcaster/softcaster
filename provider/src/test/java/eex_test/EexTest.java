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
        EexProvider provider = EexProvider.getInstance();
        double value = provider.getMktQuote("FDBM@IT@Base@POWER@F@202612",FUTURES).getData().bid();
        System.out.println(value);
    }
}
