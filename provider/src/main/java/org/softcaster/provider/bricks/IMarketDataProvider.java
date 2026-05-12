/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.provider.bricks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public interface IMarketDataProvider {

    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException;

    /**
     *
     * @param info
     * @param market
     * @throws MarketDataProviderException
     */
    default void refresh(ProviderInfo info, Market market   ) throws MarketDataProviderException {
    }

    default void build(LocalDate currentDate) throws MarketDataProviderException {
    }
}
