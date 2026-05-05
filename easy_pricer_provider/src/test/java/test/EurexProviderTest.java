/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.eurex.EurexProvider;

/**
 *
 * @author ep
 */
/*
https://www.eurex.com/ex-en/markets/int/mon/3m-euro-str-futures/estr/3402482!fullOrderBook
https://www.eurex.com/ex-en/markets/int/mon/euribor-derivatives/euribor/40828!fullOrderBook
https://www.eurex.com/ex-en/markets/fx/currency-pairs/34672!fullOrderBook
https://www.eurex.com/ex-en/markets/fx/currency-pairs/34688!fullOrderBook

*/
public class EurexProviderTest {
    
    public static void main(String[] args) {
        EurexProvider provider = EurexProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.eurex.com";
        param.url = "https://www.eurex.com/ex-en/markets/fx/currency-pairs/34688!fullOrderBook";
        param.market = MARKETS.FUTURES;

        provider.refresh(param);
    }
}
