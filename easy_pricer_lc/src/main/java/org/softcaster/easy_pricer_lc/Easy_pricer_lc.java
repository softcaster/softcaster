/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.softcaster.easy_pricer_lc;

import org.softcaster.commons.utils.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// Scansiona tutti i componenti nei package org.softcaster (sia proc che core)
@ComponentScan(basePackages = {
    "org.softcaster.easy_pricer_lc",
    "org.softcaster.easy_pricer_mds_core",
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
// Attiva i repository JPA definiti nel JAR core
@EnableJpaRepositories("org.softcaster.core.data")
// Trova le entità (AssetClass, ecc.) nel JAR core
@EntityScan(basePackages = "org.softcaster.core.data") 
public class Easy_pricer_lc {

    public static void main(String[] args) {
        // Inizializzazione Logger
        FileUtil.initializeLogger();

        SpringApplication.run(Easy_pricer_lc.class, args);

        System.out.println("================================================");
        System.out.println("   EASY PRICER LIFECYCLE STARTED SUCCESSFULLY   ");
        System.out.println("   Endpoint: http://localhost:8085/api/v1  ");
        System.out.println("================================================");
    }
    
}
