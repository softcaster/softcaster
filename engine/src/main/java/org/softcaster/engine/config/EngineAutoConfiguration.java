package org.softcaster.engine.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.softcaster.engine.analytics.BlackAndScholesPricer;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.analytics.CRRBinomialPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.analytics.LoanPricer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "org.softcaster.engine")
@EnableConfigurationProperties(BinomialProperties.class)
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

    @Bean(name = "gakPricer")
    @ConditionalOnMissingBean
    public GarmanKohlhagenPricer garmanKohlhagenPricer() {
        return new GarmanKohlhagenPricer();
    }

    // Cox-Ross-Rubinstein
    @Bean(name = "crrPricer")
    @ConditionalOnMissingBean
    public CRRBinomialPricer CRRBinomialPricer(BinomialProperties properties) {
        return new CRRBinomialPricer(properties.getSteps()
        );
    }
}
