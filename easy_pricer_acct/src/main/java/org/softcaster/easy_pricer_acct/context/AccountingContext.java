/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.AccountingEventMtm;
import org.softcaster.core.data.account.JournalEntryLines;
import org.softcaster.core.data.account.JournalEntryLinesDAO;
import org.softcaster.core.data.account.PositionAccountingEvent;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.NormalBalance;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;

public class AccountingContext {

    private final FinancialTxn txn;
    private final JournalDsl journal;
    private final AccountingEvent event;
    private AccountingPhase accountingPhase;
    private final Integer masterDatacurrency;
    private final Integer settlementCurrency;
    // decimali della settlement currency (currency.decimal_places)
    private final int currencyScale;
    // evento MTM: ultima fotografia gia' contabilizzata della posizione
    private AccountingEventMtm previousMtm;

    public AccountingContext(FinancialTxn txn, JournalDsl journal, Currency masterDataCurrency, Currency settlementCurrency, AccountingEvent event) {
        this.txn = txn;
        this.journal = journal;
        this.masterDatacurrency = masterDataCurrency.getIdCurrency();
        this.settlementCurrency = settlementCurrency.getIdCurrency();
        this.currencyScale = settlementCurrency.getDecimalPlaces() == null ? DEFAULT_SCALE : settlementCurrency.getDecimalPlaces();
        this.event = event;
        this.accountingPhase = AccountingPhase.NONE;
    }

    private static final int DEFAULT_SCALE = 2;

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
        if (txn == null || txn.getComponents() == null || txn.getComponents().isEmpty()) {
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
        if (txn == null || txn.getComponents() == null || txn.getComponents().isEmpty()) {
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
            // Stessa colonna (DEBIT o CREDIT), importo col segno invertito (storno in
            // negativo). L'importo originale puo' essere gia' negativo: le righe con
            // cui il settlement elimina il memo sono esse stesse storni.
            double debit = oldLine.getDebitAmount() == null ? 0. : oldLine.getDebitAmount();
            double credit = oldLine.getCreditAmount() == null ? 0. : oldLine.getCreditAmount();

            // Inseriamo la linea di storno nel DSL contabile corrente.
            // Passiamo l'ID dello slot convertito in stringa con un prefisso speciale "SLOT:"
            // per istruire il metodo addJournalEntrie a non fare il lookup del mapping.
            String slotKey = "SLOT:" + oldLine.getAccountSlot() + "@" + oldLine.getDescription();
            if (debit != 0.) {
                this.journal.debit(slotKey, -debit, oldLine.getCurrency());
            } else {
                this.journal.credit(slotKey, -credit, oldLine.getCurrency());
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
    public Integer getMasterDataCurrency() {
        return masterDatacurrency;
    }

    /**
     * @return the systemCurrency
     */
    public Integer getSettlementCurrency() {
        return settlementCurrency;
    }

    public double getMultiplier() {
        if (txn != null) {
            return txn.getMasterData().getMultiplier();
        } else {
            return 1.;
        }
    }

    /**
     * Decimali degli importi nella settlement currency (currency.decimal_places,
     * 2 se non indicati: JPY 0, EUR 2...). isAccounting non distingue ancora
     * una scala diversa per la contabilita'.
     */
    public int getCurrencyScale(boolean isAccounting) {
        return currencyScale;
    }

    /**
     * Cambio della transazione verso la System Currency (financial_txn.fx_rate,
     * fissato all'inserimento); 1 se l'evento non ha transazione o il cambio
     * manca.
     */
    public double getFxRate() {
        if (txn == null || txn.getFxRate() == null || txn.getFxRate() <= 0.) {
            return 1.;
        }
        return txn.getFxRate();
    }

    /**
     * Nominale con segno (negativo = posizione corta), senza multiplier: quello
     * dell'evento di posizione (PositionAccountingEvent: ratei, cedole, MTM),
     * altrimenti la quantita' della transazione. Senza nessuno dei due
     * l'evento fallisce, per non contabilizzare silenziosamente a zero.
     */
    public double getOutstandingNominal() {
        if (event instanceof PositionAccountingEvent positionEvent && positionEvent.getAccountingNominal() != null) {
            return positionEvent.getAccountingNominal();
        }
        if (txn != null) {
            return txn.getTxnSide() == TxnSide.SELL ? -txn.getQuantity() : txn.getQuantity();
        }
        throw new AccountingException("No nominal for accounting event: " + event.getEventId());
    }

    public double getAccruedInterestAmount() {
        double accruedInterestAmount=0.;
        if (event instanceof AccountingEventAccruals eventAccrual) {
            if (eventAccrual != null) {
                accruedInterestAmount = eventAccrual.getAccrualAmount();
            }
        }
        return accruedInterestAmount;
    }
    
    public double getCouponAmount() {
        double couponAmount = 0.;
        if (event.getEventType() == EventType.COUPON) {
            if (event instanceof AccountingEventAccruals eventAccrual) {
                if (eventAccrual != null) {
                    couponAmount = eventAccrual.getAccrualAmount();
                }
            }
        }
        return couponAmount;
    }

    /**
     * Ultima fotografia MTM gia' contabilizzata della stessa posizione
     * (MtmAccountingEventService), null se e' la prima.
     */
    public void setPreviousMtm(AccountingEventMtm previousMtm) {
        this.previousMtm = previousMtm;
    }

    /**
     * Variazione del risultato complessivo della posizione (realizzato + non
     * realizzato) rispetto all'ultima fotografia contabilizzata: per i future
     * e' il margine di variazione da regolare. 0 per gli eventi non MTM.
     */
    public double getMtmVariation() {
        if (!(event instanceof AccountingEventMtm current)) {
            return 0.;
        }
        return current.totalPnl() - (previousMtm == null ? 0. : previousMtm.totalPnl());
    }

    /**
     * Variazione del non realizzato rispetto all'ultima fotografia
     * contabilizzata: per i titoli e' la rettifica di fair value del giorno
     * (il realizzato passa dalla vendita). 0 per gli eventi non MTM.
     */
    public double getUnrealizedVariation() {
        if (!(event instanceof AccountingEventMtm current)) {
            return 0.;
        }
        return nz(current.getUnrealizedPnl()) - (previousMtm == null ? 0. : nz(previousMtm.getUnrealizedPnl()));
    }

    private static double nz(Double value) {
        return value == null ? 0. : value;
    }

    public TxnSide getPositionSide() {
        TxnSide side = TxnSide.BUY;
        if (txn != null) {
            side = txn.getTxnSide();
        } else if (event instanceof PositionAccountingEvent) {
            side = getOutstandingNominal() < 0. ? TxnSide.SELL : TxnSide.BUY;
        }
        return side;
    }
}
