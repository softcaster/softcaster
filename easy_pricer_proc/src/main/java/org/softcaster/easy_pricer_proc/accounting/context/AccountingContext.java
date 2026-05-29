/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.accounting.context;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.easy_pricer_proc.accounting.AccountingEvent;

/**
 *
 * @author ep
 */
public class AccountingContext {

    private final FinancialTxn txn;
    private final JournalDsl journal;
    private final AccountingEvent  accountingEvent;
    
    public AccountingContext(FinancialTxn txn, JournalDsl journal, AccountingEvent  event) {
        this.txn = txn;
        this.journal = journal;
        this.accountingEvent = event;
    }
    /**
     * @return the txn
     */
    public FinancialTxn getTxn() {
        return txn;
    }
    
    /**
     * @return the event
     */
    public AccountingEvent getAccountingEvent() {
        return accountingEvent;
    }

    /**
     * @return the journal
     */
    public JournalDsl getJournal() {
        return journal;
    }
}
