package org.softcaster.engine.config;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = "org.softcaster.org.engine") 
public class EngineAutoConfiguration {
    // Qui puoi anche definire @Bean espliciti se preferisci
}
