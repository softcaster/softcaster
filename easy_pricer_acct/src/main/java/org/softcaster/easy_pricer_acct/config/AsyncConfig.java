/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {

    @Bean(name = "acctEventExecutor")
    public TaskExecutor acctEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);       // Numero minimo di thread sempre attivi
        executor.setMaxPoolSize(10);      // Numero massimo di thread in caso di carico
        executor.setQueueCapacity(500);   // Coda per gli eventi in attesa di un thread libero
        executor.setThreadNamePrefix("AcctExecutor-");
        executor.initialize();
        return executor;
    }
}
