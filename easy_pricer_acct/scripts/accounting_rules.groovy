
import org.softcaster.engine.enums.EventType

if (ctx.eventType == EventType.TRADE_EXECUTED) {

    ctx.journal.debit(
        "BOND_POSITION",
        ctx.txn.quantity * ctx.txn.price * 2
    )

    ctx.journal.credit(
        "CASH",
        ctx.txn.quantity * ctx.txn.price * 2
    )
}