/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.engine.enums.EventType;
/**
 *
 * @author ep
 */
public class AccountingContext {

    private final FinancialTxn txn;
    private final JournalDsl journal;
    private final EventType eventType;
    
    public AccountingContext(FinancialTxn txn, JournalDsl journal, EventType  eventType) {
        this.txn = txn;
        this.journal = journal;
        this.eventType = eventType;
    }
    /**
     * @return the txn
     */
    public FinancialTxn getTxn() {
        return txn;
    }

    /**
     * @return the journal
     */
    public JournalDsl getJournal() {
        return journal;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }
}
