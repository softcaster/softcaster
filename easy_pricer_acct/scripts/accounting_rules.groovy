import org.softcaster.engine.enums.EventType

// 1. Global Event Validation
if (ctx?.event?.eventType != EventType.TRADE_EXECUTED) {
    throw new IllegalArgumentException("Accounting engine error: unsupported event type. Only TRADE_EXECUTED is allowed.")
}

// 2. Data Integrity Guard Clauses
def txn = ctx.txn
if (!txn) {
    throw new IllegalStateException("Critical error: financial transaction object (txn) is missing in the current context.")
}

if (txn.idFinancialTxn == null) {
    throw new IllegalArgumentException("Validation error: transaction ID is null.")
}

if (txn.quantity == null || txn.quantity <= 0) {
    throw new IllegalArgumentException("Validation error: transaction quantity must be greater than zero. Txn ID: ${txn.idFinancialTxn}")
}

if (txn.price == null || txn.price <= 0) {
    throw new IllegalArgumentException("Validation error: transaction price must be greater than zero. Txn ID: ${txn.idFinancialTxn}")
}

if (!txn.masterData?.assetClass?.getCode()) {
    throw new IllegalArgumentException("Validation error: missing Asset Class code for transaction ID: ${txn.idFinancialTxn}")
}

// 3. Centralized Logging / Audit Trail
// This will appear in your Java logs via the ScriptEngine execution context
// println "Global validation passed successfully for Txn ID: ${txn.idFinancialTxn} [Asset: ${txn.masterData.assetClass.getCode()}]"
