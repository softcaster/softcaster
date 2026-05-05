/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class ProcessorDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ProcessorDispatcher.class);
    
    // Spring riempie automaticamente questa mappa con tutti i Bean 
    // che implementano ITxnProcessor, usando il nome nel @Component come chiave.
    @Autowired
    private Map<String, ITxnProcessor> processorMap;

    public ITxnProcessor dispatch(String assetClass) {
        ITxnProcessor processor = processorMap.get(assetClass);

        if (processor == null) {
            log.error("### No processor found for Asset Class: {}", assetClass);
        }

        return processor;
    }
}
