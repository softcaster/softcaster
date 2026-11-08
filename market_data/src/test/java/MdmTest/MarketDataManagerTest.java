/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MdmTest;

import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.MarketDataManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.core.data", // Il pacchetto della libreria core
    "org.softcaster.engine" // Il pacchetto della libreria engine
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class MarketDataManagerTest implements CommandLineRunner {
    
    
    @Autowired
    MarketDataManagerFactory factory;
    
    public static void main(String[] args) {
        // Avvia l'applicazione tramite Spring Boot 
        SpringApplication.run(MarketDataManagerTest.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        testEurext();
     }

    private void testEurext() {
        MarketDataManager mdm = factory.getInstance("EurexMDM");
        if(mdm != null) {
            System.out.println(mdm.getMarketDataServiceId());
        }
    }
}
