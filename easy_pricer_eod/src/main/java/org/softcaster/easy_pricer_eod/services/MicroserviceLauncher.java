/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.services;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_eod.ui.views.ServiceInfo;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

class Microservice {
    private final Process process;
    private final ServiceInfo serviceInfo;
    
    public Microservice(Process process, ServiceInfo serviceInfo) {
        this.process = process;
        this.serviceInfo = serviceInfo;
    }

    /**
     * @return the process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * @return the serviceInfo
     */
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }
};

@Service
public class MicroserviceLauncher {

    private static final int ALIVE_TIMEOUT_MS = 2000;

    // Mappa per tenere traccia dei processi attivi usando il nome del servizio come chiave
    private final ConcurrentHashMap<String, Microservice> activeProcesses = new ConcurrentHashMap<>();
    
    public void startService(MicroserviceDescriptor descriptor) {
        String serviceName = descriptor.getServiceName();

        if (activeProcesses.containsKey(serviceName) && activeProcesses.get(serviceName).getProcess().isAlive()) {
            String info = "Service [" + serviceName + "] is running.";
            if(activeProcesses.get(serviceName).getServiceInfo() != null)
                activeProcesses.get(serviceName).getServiceInfo().logInfo(info);
            System.out.println(info);
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
                // Cartella del progetto del servizio (.../easy_pricer_acct/target/x.jar):
                // la contabilita' cerca gli script Groovy a partire dalla cartella di lavoro
                File jarDir = new File(descriptor.getJarPath()).getParentFile();
                if (jarDir != null && jarDir.getName().equals("target") && jarDir.getParentFile() != null) {
                    pb.directory(jarDir.getParentFile());
                }

                // 4. Avvia il processo e salva nella mappa
                Process process = pb.start();
                Microservice microservice = new Microservice(process,descriptor.getServiceInfo());
                activeProcesses.put(serviceName, microservice);
                String info = "Service [" + serviceName + "] started succesfully. PID: " + process.pid();
                if(descriptor.getServiceInfo() != null)
                    descriptor.getServiceInfo().logInfo(info);
                LoggerMgr.logInfo(info);
                System.out.println(info);

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
                if(descriptor.getServiceInfo() != null)
                    descriptor.getServiceInfo().logInfo(info);
                LoggerMgr.logInfo(info);
                System.out.println(info);

            } catch (Exception e) {
                String error = "Error starting service [" + serviceName + "]: " + e.getMessage();
                if(descriptor.getServiceInfo() != null)
                    descriptor.getServiceInfo().logError(error);
                LoggerMgr.logError(error);
                System.out.println(error);
                activeProcesses.remove(serviceName);
            }
        }).start();
    }

    /**
     * Ferma un servizio avviato da questa console.
     *
     * @return false se non c'e' un processo da fermare: il servizio e' spento
     * oppure e' stato avviato altrove (es. dall'IDE)
     */
    public boolean stopService(String serviceName) {
        Microservice microservice = activeProcesses.get(serviceName);
        if (microservice == null || !microservice.getProcess().isAlive()) {
            activeProcesses.remove(serviceName);
            return false;
        }
        microservice.getProcess().destroy();
        String info = "Service [" + serviceName + "] interrupted.";
        if(microservice.getServiceInfo() != null)
            microservice.getServiceInfo().logInfo(info);
        LoggerMgr.logInfo(info);
        System.out.println(info);
        activeProcesses.remove(serviceName);
        return true;
    }

    /**
     * Stato reale del servizio, avviato da qui o altrove: risponde all'endpoint
     * alive sulla sua porta.
     */
    public boolean isAlive(RestServiceDescriptor descriptor) {
        try {
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(ALIVE_TIMEOUT_MS);
            factory.setReadTimeout(ALIVE_TIMEOUT_MS);
            RestClient.builder().requestFactory(factory).build().get()
                    .uri("http://localhost:" + descriptor.getPort() + "/api/v1/internal/system/alive")
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void stopAllServices() {
        activeProcesses.keySet().forEach(this::stopService);
    }
}
