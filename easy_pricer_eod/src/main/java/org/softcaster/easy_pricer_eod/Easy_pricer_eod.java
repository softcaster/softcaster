/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.easy_pricer_eod;

import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// Scansiona tutti i componenti nei package org.softcaster (sia proc che core)
@ComponentScan(basePackages = {
    "org.softcaster.easy_pricer_eod",
    "org.softcaster.easy_pricer_mds_core",
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
// Attiva i repository JPA definiti nel JAR core
@EnableJpaRepositories("org.softcaster.core.data")
// Trova le entità (AssetClass, ecc.) nel JAR core
@EntityScan(basePackages = "org.softcaster.core.data")
public class Easy_pricer_eod implements CommandLineRunner {

    @Autowired
    private JEodOrchestrator eodOrchestrator;

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Modo corretto per applicazioni Swing + Spring Boot
        SpringApplicationBuilder builder = new SpringApplicationBuilder(Easy_pricer_eod.class);

        builder.headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Spostiamo tutto nel thread di Swing (EDT)
        java.awt.EventQueue.invokeLater(() -> {
            try {

                // Uso bean iniettato da Spring
                eodOrchestrator.setVisible(true);

            } catch (Exception e) {
                LoggerMgr.logError(e.getLocalizedMessage());
            }
        });
    }
}
