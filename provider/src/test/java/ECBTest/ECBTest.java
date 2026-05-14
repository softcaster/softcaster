/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ECBTest;

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
        ECBProvider provider = ECBProvider.getInstance();
        double value = provider.getOvnEstr().getData().bid();
        System.out.println(value);
    }
}
