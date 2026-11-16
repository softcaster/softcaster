import { isoDate, parseDate } from '../api/client';
import type { OtcOptionDealRequest, OtcOptionDto } from '../api/types';
import { fromPercent } from '../components/format';
import type { Registration } from '../components/RegistrationPanel';

export type OtcOptionAssetClass = 'OFX' | 'OCM';

/** Stato del form di un'opzione OTC. */
export interface OtcOptionFormState {
    underlyingCode: string;
    optionType: string;
    exerciseStyle: string;
    settlementType: string;
    strike: number | null;
    notional: number | null;
    expiryDate: Date | null;
    deliveryDate: Date | null;
    spotLag: number | null;
    expiryTime: string;
    expiryTimeZone: string;
    description: string;
    spot: number | null;
    volatilityPct: number | null;
    optionPrice: number | null;
    valuationDate: Date | null;
    premiumMode: string;
    premium: number | null;
}

const addMonths = (date: Date, months: number) => new Date(date.getFullYear(), date.getMonth() + months, date.getDate());

/** OFX: coppia di cambi, cut di New York; OCM: scadenza di un future su commodity. */
export const newOtcOptionForm = (assetClass: OtcOptionAssetClass): OtcOptionFormState => {
    const fx = assetClass === 'OFX';
    return {
        underlyingCode: fx ? 'EURUSD' : '',
        optionType: 'CALL',
        exerciseStyle: 'EUROPEAN',
        settlementType: fx ? 'PHYSICAL' : 'CASH',
        strike: fx ? 1.17 : null,
        notional: fx ? 1000000 : null,
        expiryDate: addMonths(new Date(), 3),
        deliveryDate: null,
        spotLag: null,
        expiryTime: fx ? '10:00' : '',
        expiryTimeZone: fx ? 'America/New_York' : '',
        description: '',
        spot: null,
        volatilityPct: null,
        optionPrice: null,
        valuationDate: null,
        premiumMode: fx ? 'UNIT' : 'AMOUNT',
        premium: null,
    };
};

/** Form di un'opzione registrata. */
export const otcOptionFormFromDto = (dto: OtcOptionDto, current: OtcOptionFormState): OtcOptionFormState => ({
    ...current,
    underlyingCode: dto.underlyingCode,
    optionType: dto.optionType,
    exerciseStyle: dto.exerciseStyle,
    settlementType: dto.settlementType,
    strike: dto.strike,
    notional: dto.notional,
    expiryDate: parseDate(dto.expiryDate),
    deliveryDate: parseDate(dto.deliveryDate),
    spotLag: dto.spotLag,
    expiryTime: dto.expiryTime ? dto.expiryTime.substring(0, 5) : '',
    expiryTimeZone: dto.expiryTimeZone ?? '',
    description: dto.description,
});

/** Premio totale dalla forma indicata (per il riepilogo nel form). */
export const totalPremium = (f: OtcOptionFormState): number | null => {
    if (f.premium === null || f.notional === null) {
        return null;
    }
    switch (f.premiumMode) {
        case 'UNIT':
            return f.premium * f.notional;
        case 'PERCENT':
            return f.strike === null ? null : f.premium / 100 * f.notional * f.strike;
        default:
            return f.premium;
    }
};

/**
 * Richiesta REST. Senza registrazione (prezzo) la data dell'operazione e'
 * la data di valutazione (vuota = data ufficiale).
 */
export const otcOptionRequest = (f: OtcOptionFormState, reg?: Registration): OtcOptionDealRequest => ({
    underlyingCode: f.underlyingCode,
    optionType: f.optionType,
    exerciseStyle: f.exerciseStyle,
    settlementType: f.settlementType,
    strike: f.strike,
    notional: f.notional,
    expiryDate: isoDate(f.expiryDate),
    deliveryDate: isoDate(f.deliveryDate),
    spotLag: f.spotLag,
    expiryTime: f.expiryTime ? `${f.expiryTime}:00` : null,
    expiryTimeZone: f.expiryTime ? f.expiryTimeZone || null : null,
    description: f.description || undefined,
    spot: f.spot,
    volatility: fromPercent(f.volatilityPct),
    optionPrice: f.optionPrice,
    ...(reg ? {
        side: reg.side,
        counterpartyId: reg.counterpartyId,
        positionMdId: reg.positionMdId,
        tradeDate: isoDate(reg.tradeDate),
        premium: f.premiumMode === 'AMOUNT' ? f.premium : null,
        premiumUnit: f.premiumMode === 'UNIT' ? f.premium : null,
        premiumPercent: f.premiumMode === 'PERCENT' ? f.premium : null,
    } : { tradeDate: isoDate(f.valuationDate) }),
});
