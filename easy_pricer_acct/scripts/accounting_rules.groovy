import org.softcaster.engine.enums.EventType

// 1. Global Event Validation
// Eventi mappati negli switch delle sotto-strategie
def supportedEvents = [
    EventType.TRADE_EXECUTED,
    EventType.TRADE_AMENDED,
    EventType.TRADE_CANCELED,
    EventType.MTM,
    EventType.ROLLOVER,
    EventType.ACCRUAL,
    EventType.COUPON,
    EventType.SETTLEMENT,
    EventType.MATURITY
]

// CONTROLLO GLOBALE AD ALTA EFFICIENZA
// Se l'evento corrente non è contenuto nella lista, lancia l'eccezione bloccante
if (!supportedEvents.contains(ctx?.event?.eventType)) {
    throw new IllegalArgumentException(
        "Accounting engine error: unsupported event type [${ctx?.event?.eventType}]. " +
        "This event is not handled by the current accounting strategies."
    )
}

// 2. Data Integrity Guard Clauses
def txn = ctx.txn
// Solo eventi di trade valorizzano ctx
if (txn != null) {

    if (txn.idFinancialTxn == null) {
        throw new IllegalArgumentException("Validation error: transaction ID is null.")
    }

    if (txn.quantity == null || txn.quantity <= 0) {
        throw new IllegalArgumentException("Validation error: transaction quantity must be greater than zero. Txn ID: ${txn.idFinancialTxn}")
    }

    if (!txn.masterData?.assetClass?.getCode()) {
        throw new IllegalArgumentException("Validation error: missing Asset Class code for transaction ID: ${txn.idFinancialTxn}")
    }

    // Accumulator OTC: il prezzo e' il premio iniziale, che puo' essere nullo
    // o negativo (incassato da chi compra)
    def premiumAssetClasses = ['FXA', 'CMA']

    if (txn.price == null) {
        throw new IllegalArgumentException("Validation error: transaction price is null. Txn ID: ${txn.idFinancialTxn}")
    }

    // Deal di scadenza generati dalla chiusura (origin EOD): un'opzione che
    // scade fuori dal denaro si chiude a prezzo zero
    boolean eodZeroPrice = txn.txnOrigin == org.softcaster.engine.enums.TxnOrigin.EOD && txn.price == 0
    if (txn.price < 0 && !premiumAssetClasses.contains(txn.masterData.assetClass.getCode())
            || txn.price == 0 && !eodZeroPrice && !premiumAssetClasses.contains(txn.masterData.assetClass.getCode())) {
        throw new IllegalArgumentException("Validation error: transaction price must be greater than zero. Txn ID: ${txn.idFinancialTxn}")
    }
}
// 3. Centralized Logging / Audit Trail
// This will appear in your Java logs via the ScriptEngine execution context
// println "Global validation passed successfully for Txn ID: ${txn.idFinancialTxn} [Asset: ${txn.masterData.assetClass.getCode()}]"
