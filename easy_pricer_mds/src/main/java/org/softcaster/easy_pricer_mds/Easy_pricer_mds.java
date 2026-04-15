/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.easy_pricer_mds;

import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.MarketDataProviderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author ep
 */
// Al momento escludo DB
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication()
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.easy_pricer_mds",
    "org.softcaster.easy_pricer_core" // Il pacchetto della LIBRERIA
})
@EntityScan("org.softcaster.easy_pricer_core.data")
@EnableJpaRepositories("org.softcaster.easy_pricer_core.data")

public class Easy_pricer_mds implements CommandLineRunner {

    @Autowired
    private JMarketDataService marketDataService;
    
    public static void main(String[] args) {
        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();

        // Modo corretto per applicazioni Swing + Spring Boot
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Easy_pricer_mds.class);
        
        builder.headless(false) 
               .run(args);    
    }

    @Override
    public void run(String... args) throws Exception {
        // Spostiamo tutto nel thread di Swing (EDT)
        java.awt.EventQueue.invokeLater(() -> {
            try {
                
                // Uso bean iniettato da Spring
                marketDataService.setVisible(true);

            } catch (Exception e) {
                LoggerMgr.logError(e.getLocalizedMessage());
            }
        });
    }
}
