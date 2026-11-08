/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.config;

import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.engine.config.EngineAutoConfiguration;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.services.Eurext.EurexMDM;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(after = EngineAutoConfiguration.class)
@EnableCaching // Abilita il supporto alla cache di Spring
@ComponentScan(basePackages = "org.softcaster.market_data")
public class MarketDataAutoConfiguration {
    
    @Bean(name = "EurexMDM")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public EurexMDM eurexMDM() {
        RequestParamManager rpm = new RequestParamManager();
        return new EurexMDM(rpm);
    }
}
