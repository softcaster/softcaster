/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.math.BigDecimal;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.engine.enums.TxnComponentType;

/**
 *
 * @author ep
 */
public class AccountingContext {

    private final FinancialTxn txn;
    private final JournalDsl journal;
    private final AccountingEvent event;

    public AccountingContext(FinancialTxn txn, JournalDsl journal, AccountingEvent event) {
        this.txn = txn;
        this.journal = journal;
        this.event = event;
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
     * @return the event
     */
    public AccountingEvent getEvent() {
        return event;
    }

    /**
     * Helper per estrarre in sicurezza il rateo d'interesse dai componenti
     * della transazione, senza inquinare l'entità FinancialTxn.
     * @return 
     */
    public double getBondAccruedInterest() {
        if (txn.getComponents() == null || txn.getComponents().isEmpty()) {
            return 0.0;
        }

        // Cerchiamo il componente confrontando direttamente l'Enum del tipo
        return txn.getComponents().stream()
                .filter(c -> c.getComponentType() == TxnComponentType.BOND_ACCRUAL)
                .map(FinancialTxnComponent::getAmount)
                .filter(amount -> amount != null)
                .findFirst()
                .map(BigDecimal::doubleValue) // Converte BigDecimal in double per facilitare Groovy
                .orElse(0.0);
    }
}
