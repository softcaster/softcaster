/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.config;

import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.YieldCurveBuilder;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxFutureCalculator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author softc
 */
@AutoConfiguration
@EnableCaching // Abilita il supporto alla cache di Spring
@ComponentScan(basePackages = "org.softcaster.easy_pricer_mds_core")
public class MdsCoreAutoConfiguration {
    
    @Bean(name = "marketDataService")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public MarketDataService marketDataService() {
        // Qui hai il controllo totale: puoi passare parametri al costruttore,
        // settare variabili, o loggare l'inizializzazione.
        return new MarketDataService();
    }
    
    @Bean(name = "bondForwardCalculator")
    @ConditionalOnMissingBean 
    public BondForwardCalculator bondForwardCalculator() {
        return new BondForwardCalculator();
    }
    
    @Bean(name = "fxFutureCalculator")
    @ConditionalOnMissingBean 
    public FxFutureCalculator fxFutureCalculator() {
        return new FxFutureCalculator();
    }
    
    @Bean(name = "bondCalculator")
    @ConditionalOnMissingBean 
    public BondCalculator bondCalculator() {
        return new BondCalculator();
    }
    
    @Bean(name = "YieldCurveBuilder")
    @ConditionalOnMissingBean 
    public YieldCurveBuilder yieldCurveBuilder() {
        return new YieldCurveBuilder();
    }
    
    
}
