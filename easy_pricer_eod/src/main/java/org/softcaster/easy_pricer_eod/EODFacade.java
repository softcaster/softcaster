/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod;

import org.softcaster.easy_pricer_eod.services.MicroserviceLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EODFacade {

    @Autowired
    private MicroserviceLauncher microserviceLauncher;

    /**
     * @return the microserviceLauncher
     */
    public MicroserviceLauncher getMicroserviceLauncher() {
        return microserviceLauncher;
    }
}
