import { parseDate } from '../api/client';
import type { FinancialTxnDto } from '../api/types';
import type { Registration } from '../components/RegistrationPanel';

/** Dati di registrazione di una transazione esistente (txnSide 1 = BUY, 2 = SELL). */
export const registrationFromTxn = (t: FinancialTxnDto): Registration => ({
    side: t.txnSide === 1 ? 'BUY' : 'SELL',
    counterpartyId: t.counterpartyId,
    positionMdId: t.positionMdId,
    tradeDate: parseDate(t.tradeDate ? String(t.tradeDate).substring(0, 10) : null),
});
