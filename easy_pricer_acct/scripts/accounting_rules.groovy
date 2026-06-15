
import org.softcaster.engine.enums.EventType

if (ctx.event.eventType == EventType.TRADE_EXECUTED) {

    if(ctx.txn.masterData.assetClass.getCode() == "FSP") {
        // Es EUR/USD buy 100000
        // compro 100000 USD vendendo 100000/cambio EUR
        ctx.journal.debit(
        "240090",
            ctx.txn.quantity,
            ctx.txn.masterData.ccy.idCurrency
        )

        ctx.journal.credit(
        "240090",
            ctx.txn.quantity / ctx.txn.price,
            ctx.txn.masterData.bcy.idCurrency
        )
    }
}