/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_eod.ui.views.ServiceInfo;

@Service
public class MicroserviceLauncher {

    // Mappa per tenere traccia dei processi attivi usando il nome del servizio come chiave
    private final ConcurrentHashMap<String, Process> activeProcesses = new ConcurrentHashMap<>();
    private ServiceInfo serviceInfo;
    
    public void startService(MicroserviceDescriptor descriptor) {
        String serviceName = descriptor.getServiceName();

        if (activeProcesses.containsKey(serviceName) && activeProcesses.get(serviceName).isAlive()) {
            System.out.println("Service [" + serviceName + "] is running.");
            return;
        }

        new Thread(() -> {
            try {
                // 1. Costruisce i parametri del comando di base
                List<String> command = new ArrayList<>();
                command.add("java");
                command.add("-jar");
                command.add(descriptor.getJarPath());
                command.add("--spring.profiles.active=" + descriptor.getActiveProfile());

                // 2. Aggiunge eventuali argomenti aggiuntivi
                if (descriptor.getAdditionalArgs() != null) {
                    command.addAll(List.of(descriptor.getAdditionalArgs()));
                }

                // 3. Configura il ProcessBuilder
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);

                // 4. Avvia il processo e salva nella mappa
                Process process = pb.start();
                activeProcesses.put(serviceName, process);
                String info = "Service [" + serviceName + "] started succesfully. PID: " + process.pid();
                serviceInfo.logInfo(info);
                LoggerMgr.logInfo(info);

                // 5. Consuma l'output log in tempo reale
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[" + serviceName + "] " + line);
                    }
                }

                // Rimozione dalla mappa alla chiusura spontanea
                int exitCode = process.waitFor();
                activeProcesses.remove(serviceName);
                info = "Service [" + serviceName + "] interrupted with code: " + exitCode;
                serviceInfo.logInfo(info);
                LoggerMgr.logInfo(info);

            } catch (Exception e) {
                String error = "Error starting service [" + serviceName + "]: " + e.getMessage();
                serviceInfo.logError(error);
                LoggerMgr.logError(error);
                activeProcesses.remove(serviceName);
            }
        }).start();
    }

    public void stopService(String serviceName) {
        Process process = activeProcesses.get(serviceName);
        if (process != null && process.isAlive()) {
            process.destroy();
            activeProcesses.remove(serviceName);
            String info = "Service [" + serviceName + "] interrupted.";
            serviceInfo.logInfo(info);
            LoggerMgr.logInfo(info);
        }
    }

    public void stopAllServices() {
        activeProcesses.keySet().forEach(this::stopService);
    }
    
    public void addLogger(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
