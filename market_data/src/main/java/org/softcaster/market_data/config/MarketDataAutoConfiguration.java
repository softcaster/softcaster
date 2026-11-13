/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.config;

import org.softcaster.engine.config.EngineAutoConfiguration;
import org.softcaster.market_data.core.MarketDataManagerProvider;
import org.softcaster.market_data.core.MarketDataProviderService;
import org.softcaster.market_data.services.BlueGammaOis.BlueGammaOis;
import org.softcaster.market_data.services.Bondblox.BondBlox;
import org.softcaster.market_data.services.BorsaItaliana.BorsaItaliana;
import org.softcaster.market_data.services.Cnbc.Cnbc;
import org.softcaster.market_data.services.Ecb.Ecb;
import org.softcaster.market_data.services.Eurext.Eurex;
import org.softcaster.market_data.services.MutuiIt.MutuiIt;
import org.softcaster.market_data.services.Twelvedata.Twelvedata;
import org.softcaster.market_data.services.cme.CmeGroup;
import org.softcaster.market_data.services.cme.Csv.Csv;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(after = EngineAutoConfiguration.class)
@EnableCaching // Abilita il supporto alla cache di Spring
@ComponentScan(basePackages = "org.softcaster.market_data")
public class MarketDataAutoConfiguration {

    @Bean(name = "MarketDataProviderService")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public MarketDataProviderService marketDataProviderService() {
        return new MarketDataProviderService();
    }
    
    @Bean(name = "EurexProvider")
    public MarketDataManagerProvider eurexProvider() {

        return () -> new Eurex();
    }

    @Bean(name = "BorsaItalianaProvider")
    public MarketDataManagerProvider borsaItalianaProvider() {

        return () -> new BorsaItaliana();
    }

    @Bean(name = "TwelvedataProvider")
    public MarketDataManagerProvider twelvedataProvider() {

        return () -> new Twelvedata();
    }
    
    @Bean(name = "CmeGroupProvider")
    public MarketDataManagerProvider cmeGroupProvider() {

        return () -> new CmeGroup();
    }
    
    @Bean(name = "MutuiItProvider")
    public MarketDataManagerProvider mutuiItProvider() {

        return () -> new MutuiIt();
    }

    @Bean(name = "EcbProvider")
    public MarketDataManagerProvider ecbProvider() {

        return () -> new Ecb();
    }

    @Bean(name = "BlueGammaOisProvider")
    public MarketDataManagerProvider blueGammaOisProvider() {

        return () -> new BlueGammaOis();
    }

    @Bean(name = "CnbcProvider")
    public MarketDataManagerProvider cnbcProvider() {

        return () -> new Cnbc();
    }

    @Bean(name = "CsvProvider")
    public MarketDataManagerProvider csvProvider() {

        return () -> new Csv();
    }

    @Bean(name = "BondBloxProvider")
    public MarketDataManagerProvider bondBloxProvider() {

        return () -> new BondBlox();
    }
}
