/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bondblox_test;

import org.softcaster.provider.bondblox.BondBloxProvider;
import org.softcaster.provider.bondblox.RefDatum;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author ep
 */
public class BondBloxText {
    
    public static void main(String[] args) {
        testBondBlox();
    }

    private static void testBondBlox() {
        BondBloxProvider provider = BondBloxProvider.getInstance();
        RefDatum value = provider.getRefDatum("US912810QL52", Market.BONDS);
        System.out.println(value.coupon);
        System.out.println(value.issueDate);
        System.out.println(value.maturityDate);
    }
}
