package org.softcaster.engine.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.softcaster.engine.analytics.BlackAndScholesPricer;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.analytics.LoanPricer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = "org.softcaster.engine")
public class EngineAutoConfiguration {

    @Bean(name = "btpPricer")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public BondPricer bondPricer() {
        // Qui hai il controllo totale: puoi passare parametri al costruttore,
        // settare variabili, o loggare l'inizializzazione.
        return new BondPricer();
    }
    
    @Bean(name = "loanPricer")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public LoanPricer loanPricer() {
        return new LoanPricer();
    }

    @Bean(name = "basPricer")
    @ConditionalOnMissingBean 
    public BlackAndScholesPricer blackAndScholesPricer() {
        return new BlackAndScholesPricer();
    }
    
    @Bean(name = "ghPricer")
    @ConditionalOnMissingBean 
    public GarmanKohlhagenPricer garmanKohlhagenPricer() {
        return new GarmanKohlhagenPricer();
    }
}
