/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.PositionDetail;

/**
 *
 * @author ep
 */
public interface ITxnProcessor {

    void process(FinancialTxn txn, PositionDetail position);
}
