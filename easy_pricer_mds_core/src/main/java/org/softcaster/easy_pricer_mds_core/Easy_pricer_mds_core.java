/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.easy_pricer_mds_core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.marketdataprovider.REQUEST_TYPE;

/**
 *
 * @author ep
 */
public class Easy_pricer_mds_core {

    public static void main(String[] args) {
        Map<String, List<String>> tokenList = new HashMap<>();
        tokenList.computeIfAbsent("EuroNextProvider", k -> new ArrayList<>()).add("IT0005684888-MOTX");
        MarketDataService mds = MarketDataService.getInstance();
        mds.updateBondPrice(tokenList, null);

        System.out.println(mds.getSpotPrice("IT0005684888-MOTX", REQUEST_TYPE.BID));
        System.out.println(mds.getSpotPrice("IT0005684888-MOTX", REQUEST_TYPE.ASK));
    }
}
