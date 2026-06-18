/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs.services;

import jakarta.transaction.Transactional;
import org.softcaster.core.data.PositionDetail;
import org.springframework.stereotype.Service;

@Service
public class MtmService {

    // ogni position viene elaborata e committata singolarmente
    @Transactional 
    public void evaluatePosition(PositionDetail position) {
        
    }
}
