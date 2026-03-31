/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.easy_pricer_core_test;

import org.softcaster.easy_pricer_core.data.AssetClass;
import org.softcaster.easy_pricer_core.data.AssetClassDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 *
 * @author ep
 */
@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.easy_pricer_core_test", // Il pacchetto dell'app di test
    "org.softcaster.easy_pricer_core" // Il pacchetto della LIBRERIA
})
@EntityScan("org.softcaster.easy_pricer_core.data")
@EnableJpaRepositories("org.softcaster.easy_pricer_core.data")
public class Easy_pricer_core_test implements CommandLineRunner {

    @Autowired
    private AssetClassDAO dao;

    @Autowired
    private ConfigurableApplicationContext context;

    public static void main(String[] args) {

        SpringApplication.run(Easy_pricer_core_test.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            AssetClass assetClass = dao.findByIdAssetClass(18);
            System.out.println(assetClass.getDescription());
            assetClass = dao.findByIdAssetClass(11);
            System.out.println(assetClass.getDescription());
        } finally {
            // Chiude l'app in modo ordinato, notificando tutti i bean
            SpringApplication.exit(context, () -> 0);
        }
    }
}
