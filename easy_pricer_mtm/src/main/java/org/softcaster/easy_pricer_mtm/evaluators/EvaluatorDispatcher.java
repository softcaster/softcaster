/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorDispatcher {

    private static final Logger log = LoggerFactory.getLogger(EvaluatorDispatcher.class);
    
    // Spring riempie automaticamente questa mappa con tutti i Bean 
    // che implementano IPositionEvaluator, usando il nome nel @Component come chiave.
    @Autowired
    private Map<String, IPositionEvaluator> valuetorMap;

    public IPositionEvaluator dispatch(String assetClass) {
        IPositionEvaluator valuator = valuetorMap.get(assetClass);

        if (valuator == null) {
            log.error("### No valuator found for Asset Class: {}", assetClass);
        }

        return valuator;
    }
    
}
