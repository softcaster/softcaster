import type { Option } from '../api/types';

// Mutui
export const RATE_TYPES: Option[] = [{ label: 'Fisso', value: 'FIXED' }, { label: 'Variabile', value: 'FLOATING' }];
export const AMORTIZATIONS: Option[] = [
    { label: 'Francese (rata costante)', value: 'SAS' },
    { label: 'Italiano (quota capitale costante)', value: 'SLP' },
    { label: 'Bullet (capitale a scadenza)', value: 'IOL' },
];
export const LOAN_FREQUENCIES: Option[] = [
    { label: 'Mensile', value: 'MONTHLY' }, { label: 'Bimestrale', value: 'BI-MONTHLY' },
    { label: 'Trimestrale', value: 'QUARTERLY' }, { label: 'Semestrale', value: 'SEMI-ANNUAL' }, { label: 'Annuale', value: 'ANNUAL' },
];
export const DAYCOUNTS: Option[] = [
    { label: '30/360 Euro', value: 'EUR_30_360' }, { label: 'ACT/360', value: 'ACT_360' }, { label: 'ACT/365', value: 'ACT_365' },
];
export const ROLLS: Option[] = [
    { label: 'Non rettificata', value: 'UNADJUSTED' }, { label: 'Following', value: 'FORWARD' },
    { label: 'Modified following', value: 'FORWARD-MODIFIED' },
];
export const FEE_TYPES: Option[] = [
    { label: 'Istruttoria', value: 'PROCESSING' }, { label: 'Perizia', value: 'UNDERWRITING' },
    { label: 'Imposte', value: 'TAX' }, { label: 'Spese varie', value: 'INCIDENTAL' },
    { label: 'Incasso rata', value: 'MANAGEMENT' }, { label: 'Assicurazione', value: 'INSURANCE' },
];
export const FEE_BASES: Option[] = [{ label: 'Importo', value: 'AMOUNT' }, { label: '% (punti)', value: 'PERCENT' }];
export const FEE_TIMINGS: Option[] = [{ label: 'All\'erogazione', value: 'UPFRONT' }, { label: 'Per rata', value: 'PER_INSTALLMENT' }];

// Accumulator
export const ACC_TYPES: Option[] = [{ label: 'Accumulator', value: 'ACCUMULATOR' }, { label: 'Decumulator', value: 'DECUMULATOR' }];
export const SETTLEMENTS: Option[] = [{ label: 'Fisico', value: 'PHYSICAL' }, { label: 'Per differenza', value: 'CASH' }];
export const ACC_FREQUENCIES: Option[] = [
    { label: 'Giornaliero', value: 'DAILY' }, { label: 'Settimanale', value: 'WEEKLY' },
    { label: 'Mensile', value: 'MONTHLY' }, { label: 'Alla scadenza', value: 'NONE' },
];
export const OBSERVATIONS: Option[] = [
    { label: 'Al fixing', value: 'FIXING' }, { label: 'Continua', value: 'CONTINUOUS' }, { label: 'Al regolamento', value: 'SETTLEMENT' },
];
export const KI_EFFECTS: Option[] = [
    { label: 'Nessun knock-in', value: '' }, { label: 'Attiva la leva', value: 'LEVERAGE' }, { label: 'Attiva il contratto', value: 'CONTRACT' },
];
export const GUARANTEES: Option[] = [{ label: 'Con leva', value: 'LEVERAGED' }, { label: 'Senza leva', value: 'UNLEVERAGED' }];
export const QUANTITY_MODES: Option[] = [{ label: 'Massima', value: 'MAX' }, { label: 'Per fixing', value: 'DAILY' }];
export const PREMIUM_MODES: Option[] = [{ label: 'Importo', value: 'AMOUNT' }, { label: '%', value: 'PERCENT' }];
