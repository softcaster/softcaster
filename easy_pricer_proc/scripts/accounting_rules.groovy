import org.softcaster.easy_pricer_proc.accounting.enums.AccountingEvent

if (ctx.accountingEvent == AccountingEvent.TRADE_BOOKED) {

    ctx.journal.debit(
        "BOND_POSITION",
        ctx.txn.quantity * ctx.txn.price * 2
    )

    ctx.journal.credit(
        "CASH",
        ctx.txn.quantity * ctx.txn.price * 2
    )
}