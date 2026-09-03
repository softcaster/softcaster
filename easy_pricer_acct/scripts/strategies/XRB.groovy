import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase


// ============================================================================
// XRB - Bond Accounting Script
//
// Currency model:
//
//     instrumentCcy = currency of the bond
//     accountingCcy = portfolio / accounting currency
//
// Examples:
//
//     USD bond / CHF accounting currency
//     EUR bond / CHF accounting currency
//     CHF bond / CHF accounting currency
//
// The script contains NO hardcoded EUR logic.
// ============================================================================


// ============================================================================
// 1. MATH / PRECISION
// ============================================================================

MathContext MC =
        new MathContext(18, RoundingMode.HALF_UP)


def bd = { value ->

    if (value == null)
        return BigDecimal.ZERO

    if (value instanceof BigDecimal)
        return value

    return new BigDecimal(value.toString())
}


def money = { BigDecimal value, int scale ->

    if (value == null)
        return BigDecimal.ZERO.setScale(
                scale,
                RoundingMode.HALF_UP)

    return value.setScale(
            scale,
            RoundingMode.HALF_UP)
}


// ============================================================================
// 2. CURRENCIES
// ============================================================================

/*
 * Currency of the bond.
 *
 * Example:
 *
 *     USD bond -> USD
 *     EUR bond -> EUR
 */
int instrumentCcy = ctx.masterDataCurrency


/*
 * Currency in which the portfolio/accounting books are maintained.
 *
 * Example:
 *
 *     USD bond + CHF accounting -> CHF
 */
int accountingCcy =
        ctx.getAccountingCurrency()


boolean sameCurrency =
        instrumentCcy == accountingCcy


// ============================================================================
// 3. FX RATE
// ============================================================================

/*
 * FX rate used to translate instrument currency into
 * accounting currency.
 *
 * The exact quotation convention MUST be the one defined
 * by the FX service.
 *
 * Conceptually:
 *
 *     amountAccounting =
 *          amountInstrument / fxRate
 *
 * if fxRate represents:
 *
 *     1 accounting currency = X instrument currency
 *
 * If your FX convention is the opposite, the conversion
 * must obviously be inverted.
 */

BigDecimal fxRate =
        bd(ctx.getFxRate())


if (!sameCurrency) {

    if (fxRate.compareTo(BigDecimal.ZERO) == 0) {

        throw new IllegalStateException(
                "FX rate missing or zero for XRB transaction: "
                + instrumentCcy
                + " -> "
                + accountingCcy)
    }
}


// ============================================================================
// 4. CURRENCY SCALE
// ============================================================================

int instrumentScale =
        ctx.getCurrencyScale(false)

int accountingScale =
        ctx.getCurrencyScale(true)


// ============================================================================
// 5. TRANSACTION DATA
// ============================================================================

BigDecimal quantity =
        bd(ctx.txn.quantity)

BigDecimal price =
        bd(ctx.txn.price)

BigDecimal multiplier =
        bd(ctx.multiplier)

BigDecimal accruedInterest =
        bd(ctx.bondAccruedInterest)


if (multiplier.compareTo(BigDecimal.ZERO) == 0) {
    multiplier = BigDecimal.ONE
}


TxnSide side =
        ctx.txn.txnSide


boolean isBuy =
        side == TxnSide.BUY

boolean isSell =
        side == TxnSide.SELL


// ============================================================================
// 6. ACCOUNT RESOLUTION
// ============================================================================
//
// All accounts are resolved using:
//
//     semantic mapping key + currency
//
// No GL number is hardcoded in the script.
// ============================================================================


/*
 * Position in the bond.
 *
 * Currency = instrument currency.
 */
String accBondAsset =
        accountResolver.resolve(
                "BOND_ASSET",
                instrumentCcy)


/*
 * Accrued interest.
 *
 * Currency = instrument currency.
 */
String accAccruedInterest =
        accountResolver.resolve(
                "ACCRUED_INTEREST",
                instrumentCcy)


/*
 * Interest income.
 *
 * Currency = instrument currency.
 */
String accInterestIncome =
        accountResolver.resolve(
                "INTEREST_INCOME",
                instrumentCcy)


/*
 * Position control in instrument currency.
 */
String accPositionControlInstrument =
        accountResolver.resolve(
                "POSITION_CONTROL",
                instrumentCcy)


/*
 * Position control in accounting currency.
 *
 * Only needed when instrument currency != accounting currency.
 */
String accPositionControlAccounting =
        accountResolver.resolve(
                "POSITION_CONTROL",
                accountingCcy)


/*
 * Actual cash account.
 *
 * Cash is maintained in accounting currency in this model.
 */
String accCashAccounting =
        accountResolver.resolve(
                "CASH_ACCOUNT",
                accountingCcy)


// ============================================================================
// 7. MEMO ACCOUNTS
// ============================================================================

String accCommitmentInstrument =
        accountResolver.resolve(
                "BOND_COMMITMENT",
                instrumentCcy)


String accObsClearingInstrument =
        accountResolver.resolve(
                "OBS_CLEARING",
                instrumentCcy)


String accCommitmentAccounting =
        accountResolver.resolve(
                "BOND_COMMITMENT",
                accountingCcy)


String accObsClearingAccounting =
        accountResolver.resolve(
                "OBS_CLEARING",
                accountingCcy)


// ============================================================================
// 8. TRADE AMOUNTS
// ============================================================================

/*
 * Clean bond value:
 *
 *     quantity × price × multiplier
 *
 * No rounding here.
 */
BigDecimal cleanAmount =
        quantity
                .multiply(price, MC)
                .multiply(multiplier, MC)


/*
 * Total settlement amount in instrument currency:
 *
 *     clean price value
 *     +
 *     accrued interest
 */
BigDecimal totalInstrument =
        cleanAmount
                .add(accruedInterest, MC)


// ============================================================================
// 9. TRANSLATION TO ACCOUNTING CURRENCY
// ============================================================================

BigDecimal cleanAmountAccounting
BigDecimal accruedAccounting
BigDecimal totalAccounting


if (sameCurrency) {

    cleanAmountAccounting =
            cleanAmount

    accruedAccounting =
            accruedInterest

    totalAccounting =
            totalInstrument

} else {

    /*
     * IMPORTANT:
     *
     * Do NOT round the FX rate.
     *
     * Convert first using high precision,
     * then round the resulting monetary amount.
     */

    cleanAmountAccounting =
            cleanAmount.divide(
                    fxRate,
                    MC)

    accruedAccounting =
            accruedInterest.divide(
                    fxRate,
                    MC)

    totalAccounting =
            totalInstrument.divide(
                    fxRate,
                    MC)
}


// ============================================================================
// 10. FINAL MONETARY AMOUNTS
// ============================================================================

BigDecimal cleanInstrumentMoney =
        money(
                cleanAmount,
                instrumentScale)


BigDecimal accruedInstrumentMoney =
        money(
                accruedInterest,
                instrumentScale)


BigDecimal totalInstrumentMoney =
        money(
                totalInstrument,
                instrumentScale)


BigDecimal cleanAccountingMoney =
        money(
                cleanAmountAccounting,
                accountingScale)


BigDecimal accruedAccountingMoney =
        money(
                accruedAccounting,
                accountingScale)


BigDecimal totalAccountingMoney =
        money(
                totalAccounting,
                accountingScale)


// ============================================================================
// 11. ACCOUNTING EVENTS
// ============================================================================

switch (ctx.event.eventType) {


    // ========================================================================
    // TRADE EXECUTED
    // ========================================================================

    case EventType.TRADE_EXECUTED:

        /*
         * The trade is booked off-balance.
         *
         * The memorandum structure is maintained in both
         * instrument currency and accounting currency when
         * the currencies are different.
         *
         *
         * BUY - instrument currency
         *
         *     DR  COMMITMENT
         *     CR  OBS CLEARING
         *
         *
         * BUY - accounting currency
         *
         *     DR  OBS CLEARING
         *     CR  COMMITMENT
         *
         *
         * SELL reverses the two legs.
         */


        if (isBuy) {

            // ---------------------------------------------------------------
            // Instrument currency
            // ---------------------------------------------------------------

            ctx.journal.debit(
                    accCommitmentInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)

            ctx.journal.credit(
                    accObsClearingInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)


            // ---------------------------------------------------------------
            // Accounting currency
            // ---------------------------------------------------------------

            if (!sameCurrency) {

                ctx.journal.debit(
                        accObsClearingAccounting,
                        totalAccountingMoney,
                        accountingCcy)

                ctx.journal.credit(
                        accCommitmentAccounting,
                        totalAccountingMoney,
                        accountingCcy)
            }


        } else {

            // ---------------------------------------------------------------
            // SELL - instrument currency
            // ---------------------------------------------------------------

            ctx.journal.debit(
                    accObsClearingInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)

            ctx.journal.credit(
                    accCommitmentInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)


            // ---------------------------------------------------------------
            // SELL - accounting currency
            // ---------------------------------------------------------------

            if (!sameCurrency) {

                ctx.journal.debit(
                        accCommitmentAccounting,
                        totalAccountingMoney,
                        accountingCcy)

                ctx.journal.credit(
                        accObsClearingAccounting,
                        totalAccountingMoney,
                        accountingCcy)
            }
        }

        break


    // ========================================================================
    // SETTLEMENT
    // ========================================================================

    case EventType.SETTLEMENT:

        /*
         * First remove the memorandum accounting generated
         * by TRADE_EXECUTED.
         */
        ctx.reverseJournal()


        if (isBuy) {

            // ----------------------------------------------------------------
            // 1. Put the bond on the balance sheet
            //
            //    DR BOND_ASSET
            //    DR ACCRUED_INTEREST
            //    CR POSITION_CONTROL
            //
            //    All in instrument currency.
            // ----------------------------------------------------------------

            ctx.journal.debit(
                    accBondAsset,
                    cleanInstrumentMoney,
                    instrumentCcy)


            if (accruedInstrumentMoney.compareTo(
                    BigDecimal.ZERO) != 0) {

                ctx.journal.debit(
                        accAccruedInterest,
                        accruedInstrumentMoney,
                        instrumentCcy)
            }


            ctx.journal.credit(
                    accPositionControlInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)


            // ----------------------------------------------------------------
            // 2. Settlement against actual cash
            // ----------------------------------------------------------------
            //
            // If the instrument currency is already the accounting
            // currency, this is simply:
            //
            //     DR POSITION_CONTROL
            //     CR CASH
            //
            //
            // Otherwise:
            //
            //     DR POSITION_CONTROL accounting currency
            //     CR CASH accounting currency
            // ----------------------------------------------------------------

            ctx.journal.debit(
                    accPositionControlAccounting,
                    totalAccountingMoney,
                    accountingCcy)

            ctx.journal.credit(
                    accCashAccounting,
                    totalAccountingMoney,
                    accountingCcy)


        } else {

            // ----------------------------------------------------------------
            // SELL
            //
            // Remove the bond from the position.
            // ----------------------------------------------------------------

            ctx.journal.debit(
                    accPositionControlInstrument,
                    totalInstrumentMoney,
                    instrumentCcy)


            ctx.journal.credit(
                    accBondAsset,
                    cleanInstrumentMoney,
                    instrumentCcy)


            if (accruedInstrumentMoney.compareTo(
                    BigDecimal.ZERO) != 0) {

                ctx.journal.credit(
                        accAccruedInterest,
                        accruedInstrumentMoney,
                        instrumentCcy)
            }


            // ----------------------------------------------------------------
            // Cash received
            // ----------------------------------------------------------------

            ctx.journal.debit(
                    accCashAccounting,
                    totalAccountingMoney,
                    accountingCcy)

            ctx.journal.credit(
                    accPositionControlAccounting,
                    totalAccountingMoney,
                    accountingCcy)
        }

        break


    // ========================================================================
    // ACCRUAL
    // ========================================================================

    case EventType.ACCRUAL:

        /*
         * Accrual is generated by the lifecycle engine.
         *
         * The economic amount is in instrument currency.
         *
         *     DR ACCRUED_INTEREST
         *     CR INTEREST_INCOME
         */

        BigDecimal accrualAmount =
                bd(ctx.getAccruedInterestAmount())


        accrualAmount =
                money(
                        accrualAmount,
                        instrumentScale)


        if (accrualAmount.compareTo(
                BigDecimal.ZERO) != 0) {

            ctx.journal.debit(
                    accAccruedInterest,
                    accrualAmount,
                    instrumentCcy)

            ctx.journal.credit(
                    accInterestIncome,
                    accrualAmount,
                    instrumentCcy)
        }

        break


    // ========================================================================
    // COUPON
    // ========================================================================

    case EventType.COUPON:

        /*
         * Coupon cash flow is expressed in instrument currency.
         *
         *     DR CASH
         *     CR ACCRUED_INTEREST
         *
         * The cash account itself is in accounting currency,
         * therefore a currency conversion is required when
         * instrumentCcy != accountingCcy.
         */

        BigDecimal couponAmount =
                bd(ctx.getCouponAmount())


        couponAmount =
                money(
                        couponAmount,
                        instrumentScale)


        if (couponAmount.compareTo(
                BigDecimal.ZERO) != 0) {


            // ---------------------------------------------------------------
            // Same currency
            // ---------------------------------------------------------------

            if (sameCurrency) {

                ctx.journal.debit(
                        accCashAccounting,
                        couponAmount,
                        accountingCcy)

                ctx.journal.credit(
                        accAccruedInterest,
                        couponAmount,
                        instrumentCcy)


            } else {

                // -----------------------------------------------------------
                // Foreign currency coupon
                // -----------------------------------------------------------

                BigDecimal couponAccounting =
                        couponAmount.divide(
                                fxRate,
                                MC)


                couponAccounting =
                        money(
                                couponAccounting,
                                accountingScale)


                /*
                 * Remove accrued interest in instrument currency.
                 */

                ctx.journal.debit(
                        accPositionControlInstrument,
                        couponAmount,
                        instrumentCcy)

                ctx.journal.credit(
                        accAccruedInterest,
                        couponAmount,
                        instrumentCcy)


                /*
                 * Accounting currency cash movement.
                 */

                ctx.journal.debit(
                        accCashAccounting,
                        couponAccounting,
                        accountingCcy)

                ctx.journal.credit(
                        accPositionControlAccounting,
                        couponAccounting,
                        accountingCcy)
            }
        }

        break


    // ========================================================================
    // MATURITY
    // ========================================================================

    case EventType.MATURITY:

        /*
         * At maturity we must NOT use txn.quantity.
         *
         * The lifecycle engine must provide the nominal that is
         * actually outstanding at maturity.
         */

        BigDecimal outstandingNominal =
                bd(ctx.getOutstandingNominal())


        BigDecimal maturityAmount =
                outstandingNominal
                        .multiply(
                                multiplier,
                                MC)


        maturityAmount =
                money(
                        maturityAmount,
                        instrumentScale)


        if (maturityAmount.compareTo(
                BigDecimal.ZERO) != 0) {


            // ---------------------------------------------------------------
            // Same currency
            // ---------------------------------------------------------------

            if (sameCurrency) {

                /*
                 * DR CASH
                 * CR BOND_ASSET
                 */

                ctx.journal.debit(
                        accCashAccounting,
                        maturityAmount,
                        accountingCcy)

                ctx.journal.credit(
                        accBondAsset,
                        maturityAmount,
                        instrumentCcy)


            } else {

                // -----------------------------------------------------------
                // Foreign currency maturity
                // -----------------------------------------------------------

                BigDecimal maturityAccounting =
                        maturityAmount.divide(
                                fxRate,
                                MC)


                maturityAccounting =
                        money(
                                maturityAccounting,
                                accountingScale)


                /*
                 * Remove the bond in instrument currency.
                 */

                ctx.journal.debit(
                        accPositionControlInstrument,
                        maturityAmount,
                        instrumentCcy)

                ctx.journal.credit(
                        accBondAsset,
                        maturityAmount,
                        instrumentCcy)


                /*
                 * Receive cash in accounting currency.
                 */

                ctx.journal.debit(
                        accCashAccounting,
                        maturityAccounting,
                        accountingCcy)

                ctx.journal.credit(
                        accPositionControlAccounting,
                        maturityAccounting,
                        accountingCcy)
            }
        }

        break


    // ========================================================================
    // TRADE AMENDED
    // ========================================================================

    case EventType.TRADE_AMENDED:

        /*
         * Reverse the accounting generated by the previous
         * version of the trade.
         */

        ctx.reverseJournal()

        break


    // ========================================================================
    // TRADE CANCELED
    // ========================================================================

    case EventType.TRADE_CANCELED:

        /*
         * Reverse the accounting generated by the trade.
         */

        ctx.reverseJournal()

        break


    // ========================================================================
    // UNSUPPORTED EVENT
    // ========================================================================

    default:

        throw new IllegalArgumentException(
                "Unsupported XRB accounting event: "
                + ctx.accountingEvent.eventType)
}