/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.config;

import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.FutureOptionMasterDataDAO;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.SpotOptionMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.curve.YieldCurveBuilder;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.helpers.FltSecurityMasterDataHelper;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.analytics.CRRBinomialPricer;
import org.softcaster.engine.analytics.FRBPricer;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.analytics.XRBForwardPricer;
import org.softcaster.engine.analytics.XRBPricer;
import org.softcaster.engine.config.EngineAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration(after = EngineAutoConfiguration.class)
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
    public BondForwardCalculator bondForwardCalculator(BondFutureMasterDataDAO bondFutureMasterDataDAO,
            SecurityMasterDataDAO smdDAO,
            InstrumentQuoteDAO instrumentQuoteDAO,
            MarketDataService marketDataService,
            XRBForwardPricer bondForwardPricer) {
        return new BondForwardCalculator(bondFutureMasterDataDAO,
                smdDAO,
                instrumentQuoteDAO, 
                marketDataService, 
                bondForwardPricer);
    }

    @Bean(name = "fxForwardCalculator")
    @ConditionalOnMissingBean
    public FxForwardCalculator fxForwardCalculator(
            FxFutureMasterDataDAO fxFutureMasterDataDAO,
            MarketDataService marketDataService,
            FxForwardPricer fxForwardPricer) {
        return new FxForwardCalculator(fxFutureMasterDataDAO, marketDataService, fxForwardPricer);
    }

    @Bean(name = "optionCalculator")
    @ConditionalOnMissingBean
    public OptionCalculator optionCalculator(
            FutureOptionMasterDataDAO futureOptionMasterDataDAO,
            SpotOptionMasterDataDAO spotOptionMasterDataDAO,
            FxFutureMasterDataDAO fxFutureMasterDataDAO,
            BondForwardCalculator bondForwardCalculator,
            MarketDataService marketDataService,
            Black76Pricer black76Pricer,
            GarmanKohlhagenPricer garmanKohlhagenPricer,
            CRRBinomialPricer crrPricer) {
        return new OptionCalculator(futureOptionMasterDataDAO, spotOptionMasterDataDAO, fxFutureMasterDataDAO,
                bondForwardCalculator, marketDataService, black76Pricer, garmanKohlhagenPricer, crrPricer);
    }

    @Bean(name = "cashFlowInstrumentCalculator")
    @ConditionalOnMissingBean
    public CashFlowInstrumentCalculator cashFlowInstrumentCalculator(
            SecurityMasterDataDAO smdDAO,
            XRBPricer xrbPricer,
            FRBPricer frbPricer,
            FltSecurityMasterDataHelper fltHelper) {
        return new CashFlowInstrumentCalculator(smdDAO, xrbPricer, frbPricer, fltHelper);
    }

    @Bean(name = "YieldCurveBuilder")
    @ConditionalOnMissingBean
    public YieldCurveBuilder yieldCurveBuilder() {
        return new YieldCurveBuilder();
    }

}
