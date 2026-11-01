import { isoDate, parseDate } from '../api/client';
import type { AccumulatorDealRequest, AccumulatorDto } from '../api/types';
import { fromPercent } from '../components/format';
import type { Registration } from '../components/RegistrationPanel';

/** Stato del form di un accumulator. */
export interface AccumulatorFormState {
    underlyingCode: string;
    accumulatorType: string;
    settlementType: string;
    settlementFrequency: string;
    strike: number | null;
    koBarrier: number | null;
    koObservation: string;
    kiBarrier: number | null;
    kiEffect: string;
    leverage: number | null;
    quantityMode: string;
    quantity: number | null;
    firstFixingDate: Date | null;
    lastFixingDate: Date | null;
    guaranteedPeriods: number | null;
    guaranteeType: string;
    koAccrual: boolean;
    description: string;
    spot: number | null;
    volatilityPct: number | null;
    paths: number | null;
    valuationDate: Date | null;
    premiumMode: string;
    premium: number | null;
}

/** FXA: coppia di cambi; CMA: scadenza di un future su commodity. */
export const newAccumulatorForm = (assetClass: string): AccumulatorFormState => {
    const today = new Date();
    const fx = assetClass === 'FXA';
    return {
        underlyingCode: fx ? 'EURUSD' : '',
        accumulatorType: 'ACCUMULATOR',
        settlementType: fx ? 'PHYSICAL' : 'CASH',
        settlementFrequency: 'WEEKLY',
        strike: fx ? 1.15 : null,
        koBarrier: fx ? 1.17 : null,
        koObservation: fx ? 'CONTINUOUS' : 'FIXING',
        kiBarrier: null,
        kiEffect: '',
        leverage: 2,
        quantityMode: 'MAX',
        quantity: 100000,
        firstFixingDate: today,
        lastFixingDate: new Date(today.getFullYear(), today.getMonth() + 1, today.getDate()),
        guaranteedPeriods: 2,
        guaranteeType: 'UNLEVERAGED',
        koAccrual: false,
        description: '',
        spot: null,
        volatilityPct: null,
        paths: 50000,
        valuationDate: null,
        premiumMode: 'AMOUNT',
        premium: 0,
    };
};

/** Form di un accumulator registrato (termini in sola lettura). */
export const accumulatorFormFromDto = (dto: AccumulatorDto, current: AccumulatorFormState): AccumulatorFormState => ({
    ...current,
    underlyingCode: dto.underlyingCode,
    accumulatorType: dto.accumulatorType,
    settlementType: dto.settlementType,
    settlementFrequency: dto.settlementFrequency,
    strike: dto.strike,
    koBarrier: dto.koBarrier,
    koObservation: dto.koObservation,
    kiBarrier: dto.kiBarrier,
    kiEffect: dto.kiEffect ?? '',
    leverage: dto.leverage,
    quantityMode: 'DAILY',
    quantity: dto.dailyQuantity,
    firstFixingDate: parseDate(dto.firstFixingDate),
    lastFixingDate: parseDate(dto.lastFixingDate),
    guaranteedPeriods: dto.guaranteedPeriods,
    guaranteeType: dto.guaranteeType === 'NONE' ? 'LEVERAGED' : dto.guaranteeType,
    koAccrual: dto.koAccrual,
    description: dto.description,
});

/**
 * Richiesta REST. Senza registrazione (prezzo) la data dell'operazione e'
 * la data di valutazione, o il primo fixing se e' gia' passato.
 */
export const accumulatorRequest = (f: AccumulatorFormState, reg?: Registration): AccumulatorDealRequest => {
    const pricingDate = f.valuationDate ?? (f.firstFixingDate && f.firstFixingDate < new Date() ? f.firstFixingDate : null);
    return {
        underlyingCode: f.underlyingCode,
        accumulatorType: f.accumulatorType,
        settlementType: f.settlementType,
        settlementFrequency: f.settlementFrequency,
        strike: f.strike,
        koBarrier: f.koBarrier,
        koObservation: f.koObservation,
        kiBarrier: f.kiEffect ? f.kiBarrier : null,
        kiEffect: f.kiEffect || null,
        leverage: f.leverage,
        dailyQuantity: f.quantityMode === 'DAILY' ? f.quantity : null,
        maxQuantity: f.quantityMode === 'MAX' ? f.quantity : null,
        firstFixingDate: isoDate(f.firstFixingDate),
        lastFixingDate: isoDate(f.lastFixingDate),
        guaranteedPeriods: f.guaranteedPeriods ?? 0,
        guaranteeType: (f.guaranteedPeriods ?? 0) > 0 ? f.guaranteeType : null,
        koAccrual: f.koAccrual,
        description: f.description || undefined,
        spot: f.spot,
        volatility: fromPercent(f.volatilityPct),
        paths: f.paths,
        ...(reg ? {
            side: reg.side,
            counterpartyId: reg.counterpartyId,
            positionMdId: reg.positionMdId,
            tradeDate: isoDate(reg.tradeDate),
            premium: f.premiumMode === 'AMOUNT' ? f.premium : 0,
            premiumPercent: f.premiumMode === 'PERCENT' ? f.premium : null,
        } : { tradeDate: isoDate(pricingDate) }),
    };
};
