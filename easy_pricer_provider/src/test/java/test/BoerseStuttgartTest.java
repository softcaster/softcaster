/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.softcaster.marketdataprovider.BoerseStuttgart.BoerseStuttgartProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.MARKETS;

/**
 *
 * @author ep
 */
public class BoerseStuttgartTest {
    public static void main(String[] args) {

        BoerseStuttgartProvider provider = BoerseStuttgartProvider.getInstance();
        ConnectionParam param = new ConnectionParam();

        param.baseUrl = "https://www.boerse-stuttgart.de";
        param.url = "https://www.boerse-stuttgart.de/en/products/currencies/965275-eur-usd";
        param.market = MARKETS.CURRENCIES;

        provider.refresh(param);
    }
    
}
