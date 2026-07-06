/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import javax.swing.JFrame;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
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
@SpringBootApplication
// Scansiona i pacchetti della LIBRERIA per trovare @Service, @Component, ecc.
@ComponentScan(basePackages = {
    "org.softcaster.easy_import", // Il pacchetto dell'app
    "org.softcaster.core.data", // Il pacchetto della LIBRERIA
    "org.softcaster.core.dto" // Il pacchetto della libreria core
})
@EntityScan("org.softcaster.core.data")
@EnableJpaRepositories("org.softcaster.core.data")
public class EasyImport implements CommandLineRunner {

    @Autowired
    private ImportMgr importMgr;

    public static void main(String[] args) {

        // Inizializzazione Logger
        FileUtil.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        FileUtil.initializePython();

        // Modo corretto per applicazioni Swing + Spring Boot
        SpringApplicationBuilder builder = new SpringApplicationBuilder(EasyImport.class);
        
        builder.headless(false) 
               .run(args);    
    }

    @Override
    public void run(String... args) throws Exception {
        // Spostiamo tutto nel thread di Swing (EDT)
        java.awt.EventQueue.invokeLater(() -> {
            try {
                
                // Uso bean iniettato da Spring
                importMgr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                importMgr.setVisible(true);

            } catch (Exception e) {
                LoggerMgr.logError(e.getLocalizedMessage());
            }
        });
    }
}
