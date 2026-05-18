package org.softcaster.engine.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.analytics.BlackAndScholesPricer;
import org.softcaster.engine.analytics.BondForwardPricer;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.analytics.CRRBinomialPricer;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.analytics.LoanPricer;
import org.softcaster.engine.cashflow.BackwardScheduleGenerator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = "org.softcaster.engine")
@EnableConfigurationProperties(BinomialProperties.class)
public class EngineAutoConfiguration {

    @Bean(name = "backwardScheduleGenerator")
    @ConditionalOnMissingBean // caso in cui qualche libreria volesse creare un proprio bean
    public BackwardScheduleGenerator backwardScheduleGenerator() {
        // Qui si possono passare parametri al costruttore,
        // settare variabili, o loggare l'inizializzazione.
        return new BackwardScheduleGenerator();
    }

    @Bean(name = "bondPricer")
    @ConditionalOnMissingBean 
    public BondPricer bondPricer() {
        return new BondPricer();
    }

    @Bean(name = "bondFwdPricer")
    @ConditionalOnMissingBean 
    public BondForwardPricer bondForwardPricer() {
        return new BondForwardPricer();
    }

    @Bean(name = "loanPricer")
    @ConditionalOnMissingBean 
    public LoanPricer loanPricer() {
        return new LoanPricer();
    }

    @Bean(name = "fxFwdPricer")
    @ConditionalOnMissingBean 
    public FxForwardPricer fxForwardPricer() {
        return new FxForwardPricer();
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
    public CRRBinomialPricer cRRBinomialPricer(BinomialProperties properties) {
        return new CRRBinomialPricer(properties.getSteps());
    }

    // Black-76
    @Bean(name = "b76Pricer")
    @ConditionalOnMissingBean
    public Black76Pricer black76Pricer() {
        return new Black76Pricer();
    }
}
