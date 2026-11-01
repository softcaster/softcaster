import { parseDate } from '../api/client';
import type { FinancialTxnDto } from '../api/types';
import type { Registration } from '../components/RegistrationPanel';

/**
 * Transazione modificabile o cancellabile dal deal capture (come il client
 * degli strumenti quotati): PENDING (1) si aggiorna, EXECUTED (3) va in
 * TO_AMEND / TO_CANCEL; le altre sono in sola lettura.
 */
export const isEditableTxn = (t: FinancialTxnDto): boolean => t.txnStatusId === 1 || t.txnStatusId === 3;

/** Dati di registrazione di una transazione esistente (txnSide 1 = BUY, 2 = SELL). */
export const registrationFromTxn = (t: FinancialTxnDto): Registration => ({
    side: t.txnSide === 1 ? 'BUY' : 'SELL',
    counterpartyId: t.counterpartyId,
    positionMdId: t.positionMdId,
    tradeDate: parseDate(t.tradeDate ? String(t.tradeDate).substring(0, 10) : null),
});
