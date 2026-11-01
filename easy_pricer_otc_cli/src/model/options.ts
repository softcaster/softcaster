import type { Option } from '../api/types';

// Scelte proprie del client (non enum del server); gli enum arrivano da
// /enums tramite useEnums (model/enums.ts)

// Mutui: fisso / variabile (asset class XRM / FRM)
export const RATE_TYPES: Option[] = [
    { label: 'Fixed', value: 'FIXED', icon: 'pi pi-lock' },
    { label: 'Floating', value: 'FLOATING', icon: 'pi pi-chart-line' },
];

// Accumulator: come si indica la quantita' e il premio nel form
export const QUANTITY_MODES: Option[] = [
    { label: 'Maximum', value: 'MAX', icon: 'pi pi-box' },
    { label: 'Per fixing', value: 'DAILY', icon: 'pi pi-calendar' },
];
export const PREMIUM_MODES: Option[] = [
    { label: 'Amount', value: 'AMOUNT', icon: 'pi pi-money-bill' },
    { label: '%', value: 'PERCENT', icon: 'pi pi-percentage' },
];

// Lato dell'operazione, punto di vista della banca (come SIDE_OPTIONS di easy_pricer_cli)
export const SIDES: Option[] = [
    { label: 'Buy', value: 'BUY', icon: 'pi pi-arrow-down-right text-green-500' },
    { label: 'Sell', value: 'SELL', icon: 'pi pi-arrow-up-right text-red-500' },
];
