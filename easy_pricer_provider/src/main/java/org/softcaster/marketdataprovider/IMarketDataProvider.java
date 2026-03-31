/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.marketdataprovider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

/**
 *
 * @author ep
 */
public interface IMarketDataProvider {
    
    public void connect(ConnectionParam param) throws MalformedURLException, IOException;
    
    public List<DataNode> quotes(MARKETS market);

    /**
     *
     * @param param
     * @throws MarketDataProviderException
     */
    default void refresh(ConnectionParam param) throws MarketDataProviderException {
    } 

    default void build(org.softcaster.commons.types.Date currentDate) throws MarketDataProviderException {
    } 
}
