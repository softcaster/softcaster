/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ValuationStartup {

    @Autowired
    private ValuationInitializer valuationInitializer;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        valuationInitializer.init();
    }
}