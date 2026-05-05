/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.PositionDetail;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("FSP")
public class ForexTxnProcessor implements ITxnProcessor{
    
    public ForexTxnProcessor() {       
    }
    
    @Override
    public void process(FinancialTxn txn, PositionDetail position) {
    }
}
