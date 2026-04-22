/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.softcaster.easy_pricer_srv;

import org.softcaster.commons.utils.FileUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Scansiona tutti i componenti nei package org.softcaster (sia srv che core)
@ComponentScan(basePackages = "org.softcaster") 
// Attiva i repository JPA definiti nel JAR core
@EnableJpaRepositories("org.softcaster.easy_pricer_core.data")
// Trova le entità (AssetClass, ecc.) nel JAR core
@EntityScan(basePackages = "org.softcaster.easy_pricer_core.data") 
public class Easy_pricer_srv {

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();
        
        SpringApplication.run(Easy_pricer_srv.class, args);
        System.out.println("===========================================");
        System.out.println("   EASY PRICER SRV STARTED SUCCESSFULLY    ");
        System.out.println("   Endpoint: http://localhost:8080         ");
        System.out.println("===========================================");
    }
}
