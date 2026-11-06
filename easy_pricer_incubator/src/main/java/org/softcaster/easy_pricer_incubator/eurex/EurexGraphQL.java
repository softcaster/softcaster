/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_incubator.eurex;

import java.util.*;

public class EurexGraphQL {

    private static void testBondFuture() throws Exception {
        EurexApiClient client = new EurexApiClient("FBTP");
        List<ContractData> data = client.executeQueryIsinPrices();
        int contractId = 0;
        if (data != null && !data.isEmpty()) {
            for (ContractData d : data) {
                if (d.isin().equals("DE000F2HLQE9")) {
                    contractId = d.contractId();
                    break;
                }
            }
        }

        Map<Integer, List<DeliverableBond>> basket = client.executeQueryDeliverables();
        if (basket != null || !basket.isEmpty()) {
            List<DeliverableBond> deliverables = basket.get(contractId);
            if (deliverables != null && !deliverables.isEmpty()) {
                for (DeliverableBond d : deliverables) {
                    System.out.println(d.bondIsin() + "\t" + d.coupon() + "\t" + d.conversionFactor());
                }
            }
        }
    }

    private static void testEuriborFuture() throws Exception {
        EurexApiClient client = new EurexApiClient("FEU3");
        List<ContractData> data = client.executeQueryIsinPrices();
        if (data != null && !data.isEmpty()) {
            for (ContractData d : data) {
                System.out.println((100. - d.price()) + "\t" + d.settlementDate());
            }
        }
    }


    
    public static void main(String[] args) throws Exception {
        //testBondFuture();
        testEuriborFuture();
    }
}
