/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.config;

/**
 *
 * @author ep
 */
import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync // <--- Fondamentale per attivare il supporto a @Async
public class AsyncConfig {

    @Bean(name = "importTaskExecutor")
    public Executor importTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. Thread minimi sempre attivi
        executor.setCorePoolSize(2);

        // 2. Massimo numero di thread (per non saturare la CPU)
        executor.setMaxPoolSize(5);

        // 3. Capacità della coda (se arrivano troppe richieste contemporanee)
        executor.setQueueCapacity(500);

        // 4. Prefisso per i log (utile per il debug)
        executor.setThreadNamePrefix("ImportWorker-");

        // 5. Gestione dello spegnimento pulito
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}
