/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.config;

import org.softcaster.engine.config.EngineAutoConfiguration;
import org.softcaster.market_data.core.MarketDataManagerProvider;
import org.softcaster.market_data.services.BorsaItaliana.BorsaItaliana;
import org.softcaster.market_data.services.Eurext.Eurex;
import org.softcaster.market_data.services.Twelvedata.Twelvedata;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(after = EngineAutoConfiguration.class)
@EnableCaching // Abilita il supporto alla cache di Spring
@ComponentScan(basePackages = "org.softcaster.market_data")
public class MarketDataAutoConfiguration {
    
    @Bean(name = "EurexProvider")
    public MarketDataManagerProvider eurexProvider() {

        return params -> new Eurex(params);
    }

    @Bean(name = "BorsaItalianaProvider")
    public MarketDataManagerProvider borsaItalianaProvider() {

        return params -> new BorsaItaliana(params);
    }

    @Bean(name = "TwelvedataProvider")
    public MarketDataManagerProvider twelvedataProvider() {

        return params -> new Twelvedata(params);
    }
}
