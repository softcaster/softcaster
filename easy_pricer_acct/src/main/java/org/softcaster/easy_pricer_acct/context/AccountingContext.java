/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.GlAccountDAO;
import org.softcaster.core.data.account.GlAccountSlots;
import org.softcaster.core.data.account.JournalEntryLines;
import org.softcaster.core.data.account.JournalEntryLinesDAO;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.NormalBalance;
import org.softcaster.engine.enums.TxnComponentType;

public class AccountingContext {

    private final FinancialTxn txn;
    private final JournalDsl journal;
    private final AccountingEvent event;
    private AccountingPhase accountingPhase;
    private Integer currency;

    public AccountingContext(FinancialTxn txn, JournalDsl journal, Integer currency, AccountingEvent event) {
        this.txn = txn;
        this.journal = journal;
        this.currency = currency;
        this.event = event;
        this.accountingPhase = AccountingPhase.NONE;
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

    public double getDailyAccrualAmount() {
        double dailyAccrualAmount = 0.;
        if (event instanceof AccountingEventAccruals accrualEvent) {
            dailyAccrualAmount = accrualEvent.getAccrualAmount();
        }

        return dailyAccrualAmount;
    }

    /**
     * Helper per estrarre in sicurezza il rateo d'interesse dai componenti
     * della transazione, senza inquinare l'entità FinancialTxn.
     *
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

    public double getFutureInitialMargin() {
        if (txn.getComponents() == null || txn.getComponents().isEmpty()) {
            return 0.0;
        }
        return txn.getComponents().stream()
                .filter(c -> c.getComponentType() == TxnComponentType.INITIAL_MARGIN)
                .map(FinancialTxnComponent::getAmount)
                .filter(Objects::nonNull)
                .findFirst()
                .map(BigDecimal::doubleValue)
                .orElse(0.0);
    }

    String getAccountCode(Integer id) {
        GlAccountDAO glAccountDAO = ApplicationContextHolder.getBean(GlAccountDAO.class);
        return glAccountDAO.findByAccountId(id).getCode();
    }

    /**
     * Recupera le vecchie linee dal database tramite il DAO/Repository e genera
     * lo storno ad importo invertito nel DSL.
     */
    public void reverseJournal() {
        // Recuperiamo le linee associate all'ID della transazione corrente
        JournalEntryLinesDAO journalEntryLinesDAO = ApplicationContextHolder.getBean(JournalEntryLinesDAO.class);
        List<JournalEntryLines> oldLines = journalEntryLinesDAO.findLinesByFinancialTxnId(this.event.getSourceId());

        if (oldLines == null || oldLines.isEmpty()) {
            String error = "No lines found Accounting Event: " + this.event.getEventId();
            throw new AccountingException(error);
        }

        for (JournalEntryLines oldLine : oldLines) {
            // Manteniamo lo stesso identico BalanceType (DEBIT o CREDIT)
            NormalBalance sameBalance = (oldLine.getDebitAmount() > 0) ? NormalBalance.DEBIT : NormalBalance.CREDIT;

            // Estraiamo l'importo positivo originario
            double originalAmount = (oldLine.getDebitAmount() > 0) ? oldLine.getDebitAmount() : oldLine.getCreditAmount();

            // Invertiamo il segno dell'importo (Storno in Nero / Negativo)
            double negativeAmount = originalAmount * (-1.0);

            // Inseriamo la linea di storno nel DSL contabile corrente.
            // Passiamo l'ID dello slot convertito in stringa con un prefisso speciale "SLOT:" 
            // per istruire il metodo addJournalEntrie a non fare il lookup del mapping.
            String slotKey = "SLOT:" + oldLine.getAccountSlot() + "@" + oldLine.getDescription();
            if (sameBalance == NormalBalance.DEBIT) {
                this.journal.debit(slotKey, negativeAmount, oldLine.getCurrency());
            } else {
                this.journal.credit(slotKey, negativeAmount, oldLine.getCurrency());
            }
        }
    }

    /**
     * @return the accountingPhase
     */
    public AccountingPhase getAccountingPhase() {
        return accountingPhase;
    }

    /**
     * @param accountingPhase the accountingPhase to set
     */
    public void setAccountingPhase(AccountingPhase accountingPhase) {
        this.accountingPhase = accountingPhase;
    }

    /**
     * @return the currency
     */
    public Integer getCurrency() {
        return currency;
    }

    /**
     * @param currency the currency to set
     */
    public void setCurrency(Integer currency) {
        this.currency = currency;
    }
}
