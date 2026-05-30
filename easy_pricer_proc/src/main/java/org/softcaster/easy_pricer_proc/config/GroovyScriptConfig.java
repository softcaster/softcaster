/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.script.ScriptEngine;

import javax.script.ScriptEngineManager;

@Configuration
public class GroovyScriptConfig {

    @Bean
    public ScriptEngine groovyScriptEngine() {
        // Viene eseguito SOLO UNA VOLTA all'avvio del server
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("groovy");

        if (engine == null) {
            throw new IllegalStateException("Can't start Groovy!");
        }
        
        return engine;
    }
}
