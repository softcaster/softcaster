/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package euronext_test;

import org.softcaster.provider.euronext.EuroNextProvider;

/**
 *
 * @author ep
 */
public class EuronextTest {
    
    public static void main(String[] args) {
        testEuronext();
    }

    private static void testEuronext() {
        
        EuroNextProvider provider = EuroNextProvider.getInstance();
        double value = provider.getBondQuote("IT0001086567").getData().bid();
        System.out.println(value);
        
        String symbol = "IT0024832682@MBTP-DMIL?fOrO=F&md=01-06-2026";
        provider = EuroNextProvider.getInstance();
        value = provider.getFutureQuote(symbol).getData().bid();
        System.out.println(value);

        value = provider.getBondQuote("IT0001086567").getData().bid();
        System.out.println(value);
    }
}
