import { useEffect, useState } from 'react';
import { apiGet } from '../api/client';
import type { EnumDto, Option } from '../api/types';

/** Enum esposti da easy_pricer_srv (/enums), vedi EnumService. */
export type EnumName =
    | 'daycountBasis' | 'rollConvention' | 'amortizationSchedule' | 'loanFrequency'
    | 'loanFeeType' | 'feeBasis' | 'feeTiming'
    | 'accumulatorType' | 'settlementType' | 'accumulatorFrequency'
    | 'barrierObservation' | 'knockInEffect' | 'guaranteeType';

export type EnumOptions = Partial<Record<EnumName, Option[]>>;

// Icone dei pulsanti di scelta: solo presentazione, restano nel client
const ICONS: Record<string, string> = {
    'accumulatorType.ACCUMULATOR': 'pi pi-plus-circle',
    'accumulatorType.DECUMULATOR': 'pi pi-minus-circle',
    'guaranteeType.LEVERAGED': 'pi pi-angle-double-up',
    'guaranteeType.UNLEVERAGED': 'pi pi-minus',
};

// Gli enum non cambiano durante la sessione: una sola richiesta per nome
const cache = new Map<EnumName, Option[]>();

const toOptions = (name: EnumName, items: EnumDto[]): Option[] =>
    items.map((e) => ({ label: e.description, value: e.code, icon: ICONS[`${name}.${e.code}`] }));

const cached = (names: EnumName[]): EnumOptions =>
    Object.fromEntries(names.filter((n) => cache.has(n)).map((n) => [n, cache.get(n)!]));

/** Voci degli enum richiesti come opzioni (label = descrizione, value = codice). */
export const useEnums = (...names: EnumName[]): EnumOptions => {
    const key = names.join(',');
    const [options, setOptions] = useState<EnumOptions>(() => cached(names));

    useEffect(() => {
        const wanted = key.split(',') as EnumName[];
        const missing = wanted.filter((n) => !cache.has(n));
        if (missing.length === 0) {
            return;
        }
        let active = true;
        apiGet<Record<string, EnumDto[]>>(`/enums?names=${missing.join(',')}`)
            .then((data) => {
                missing.forEach((n) => cache.set(n, toOptions(n, data[n] ?? [])));
                if (active) {
                    setOptions(cached(wanted));
                }
            })
            .catch((e) => console.error('Failed to fetch enums:', e));
        return () => {
            active = false;
        };
    }, [key]);

    return options;
};
