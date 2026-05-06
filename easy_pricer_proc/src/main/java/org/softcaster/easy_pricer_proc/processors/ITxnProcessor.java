/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.PositionDetail;

/**
 *
 * @author ep
 */
public interface ITxnProcessor {

    public static final int BUY = 1;
    public static final int SELL = -1;

    void process(FinancialTxn txn, PositionDetail position);
}
