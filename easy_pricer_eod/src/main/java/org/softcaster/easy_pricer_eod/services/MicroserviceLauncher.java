/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class MicroserviceLauncher {

    private Process mtmProcess;

    public void startMtmService(String jarPath, String activeProfile) {
        if (mtmProcess != null && mtmProcess.isAlive()) {
            System.out.println("Il servizio MTM è già in esecuzione!");
            return;
        }

        // Eseguiamo il lancio in un thread separato per non bloccare la GUI
        new Thread(() -> {
            try {
                // Costruisci il comando esatto: java -jar percorso/xyz.jar --spring.profiles.active=prod
                ProcessBuilder pb = new ProcessBuilder(
                    "java", 
                    "-jar", 
                    jarPath, 
                    "--spring.profiles.active=" + activeProfile
                );

                // Opzionale: imposta la cartella di lavoro del microservizio
                File jarFile = new File(jarPath);
                if (jarFile.getParentFile() != null) {
                    pb.directory(jarFile.getParentFile());
                }

                // Unisci l'error stream con l'output stream standard per leggere tutto insieme
                pb.redirectErrorStream(true);

                // Avvia fisicamente il processo del sistema operativo
                mtmProcess = pb.start();
                System.out.println("Microservizio MTM avviato con PID: " + mtmProcess.pid());

                // FONDAMENTALE: Leggi continuamente l'output del JAR, altrimenti il processo si satura e si blocca
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(mtmProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Puoi stampare sulla console dell'orchestratore o inviarlo a una JTextArea della GUI
                        System.out.println("[MTM-LOG] " + line);
                    }
                }

                // Attendi la fine del processo (se termina spontaneamente)
                int exitCode = mtmProcess.waitFor();
                System.out.println("Il servizio MTM è terminato con codice: " + exitCode);

            } catch (Exception e) {
                System.err.println("Errore durante l'avvio del servizio MTM: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Metodo per spegnere forzatamente il servizio dall'orchestratore se necessario
     */
    public void stopMtmService() {
        if (mtmProcess != null && mtmProcess.isAlive()) {
            mtmProcess.destroy(); // Invia un segnale di stop (SIGTERM)
            System.out.println("Richiesta di terminazione inviata al servizio MTM.");
        }
    }
}
