/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerDispatcher {

    private static final Logger log = LoggerFactory.getLogger(SchedulerDispatcher.class);
    
    // Spring riempie automaticamente questa mappa con tutti i Bean 
    // che implementano IPositionEvaluator, usando il nome nel @Component come chiave.
    @Autowired
    private Map<String, IScheduler> schedulersMap;

        public IScheduler dispatch(String assetClass) {
        IScheduler scheduler = schedulersMap.get(assetClass);
        
        return scheduler;
    }
    
}
