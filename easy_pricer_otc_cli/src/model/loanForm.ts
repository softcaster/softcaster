import { isoDate, parseDate } from '../api/client';
import type { LoanDealRequest, LoanDto, LoanFeeInput } from '../api/types';
import { fromPercent } from '../components/format';
import type { Registration } from '../components/RegistrationPanel';

/** Stato del form di un mutuo: percentuali come le vede l'utente (3,2 = 3,2%). */
export interface LoanFormState {
    rateType: string;
    currency: string;
    principalAmount: number | null;
    annualRate: number | null;
    disbursementDate: Date | null;
    firstPaymentDate: Date | null;
    maturityDate: Date | null;
    frequency: string;
    amortization: string;
    daycount: string;
    rollConvention: string;
    preAmortizationPeriods: number | null;
    description: string;
    fees: LoanFeeInput[];
    spreadPct: number | null;
    floorPct: number | null;
    capPct: number | null;
    indexCode: string;
    valuationDate: Date | null;
    discountCurve: string;
    discountRatePct: number | null;
    creditSpreadPct: number | null;
}

export const newLoanForm = (rateType = 'FIXED'): LoanFormState => {
    const today = new Date();
    return {
        rateType,
        currency: 'EUR',
        principalAmount: 200000,
        annualRate: 3.2,
        disbursementDate: today,
        firstPaymentDate: null,
        maturityDate: new Date(today.getFullYear() + 20, today.getMonth(), today.getDate()),
        frequency: 'MONTHLY',
        amortization: 'SAS',
        daycount: 'EUR_30_360',
        rollConvention: 'UNADJUSTED',
        preAmortizationPeriods: 0,
        description: '',
        fees: [
            { type: 'PROCESSING', basis: 'AMOUNT', timing: 'UPFRONT', value: 1500 },
            { type: 'TAX', basis: 'PERCENT', timing: 'UPFRONT', value: 0.25 },
            { type: 'MANAGEMENT', basis: 'AMOUNT', timing: 'PER_INSTALLMENT', value: 2.5 },
        ],
        spreadPct: 1.2,
        floorPct: 0,
        capPct: null,
        indexCode: '',
        valuationDate: null,
        discountCurve: '',
        discountRatePct: 3,
        creditSpreadPct: 0,
    };
};

/** Form di un mutuo registrato (termini in sola lettura, valutazione modificabile). */
export const loanFormFromDto = (dto: LoanDto, current: LoanFormState): LoanFormState => ({
    ...current,
    rateType: dto.assetClass === 'FRM' ? 'FLOATING' : 'FIXED',
    currency: dto.currency,
    principalAmount: dto.principalAmount,
    annualRate: dto.annualRate,
    disbursementDate: parseDate(dto.disbursementDate),
    firstPaymentDate: parseDate(dto.firstPaymentDate),
    maturityDate: parseDate(dto.maturityDate),
    frequency: dto.frequency,
    amortization: dto.amortization,
    daycount: dto.daycount,
    preAmortizationPeriods: dto.preAmortizationPeriods,
    description: dto.description,
    fees: dto.fees.map((f) => ({ ...f })),
    spreadPct: dto.spread === null ? null : dto.spread * 100,
    floorPct: dto.floor === null ? null : dto.floor * 100,
    capPct: dto.cap === null ? null : dto.cap * 100,
});

export const loanRequest = (f: LoanFormState, reg?: Registration): LoanDealRequest => ({
    rateType: f.rateType,
    currency: f.currency,
    principalAmount: f.principalAmount,
    annualRate: f.annualRate,
    disbursementDate: isoDate(f.disbursementDate),
    firstPaymentDate: isoDate(f.firstPaymentDate),
    maturityDate: isoDate(f.maturityDate),
    frequency: f.frequency,
    amortization: f.amortization,
    daycount: f.daycount,
    rollConvention: f.rollConvention,
    preAmortizationPeriods: f.preAmortizationPeriods ?? 0,
    description: f.description || undefined,
    fees: f.fees.filter((fee) => fee.value !== null),
    spread: f.rateType === 'FLOATING' ? fromPercent(f.spreadPct) : null,
    floor: f.rateType === 'FLOATING' ? fromPercent(f.floorPct) : null,
    cap: f.rateType === 'FLOATING' ? fromPercent(f.capPct) : null,
    indexCode: f.rateType === 'FLOATING' && f.indexCode ? f.indexCode : undefined,
    valuationDate: isoDate(f.valuationDate),
    discountCurve: f.discountCurve || undefined,
    discountRate: fromPercent(f.discountRatePct),
    creditSpread: fromPercent(f.creditSpreadPct),
    ...(reg ? {
        side: reg.side,
        counterpartyId: reg.counterpartyId,
        positionMdId: reg.positionMdId,
        tradeDate: isoDate(reg.tradeDate),
    } : {}),
});
