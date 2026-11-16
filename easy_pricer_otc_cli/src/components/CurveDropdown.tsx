import { useEffect, useState } from 'react';
import { Dropdown } from 'primereact/dropdown';
import { fetchDiscountCurves } from '../api/services';
import type { DiscountCurveDto } from '../api/types';

// Le curve non cambiano durante la sessione: una sola richiesta
let cache: Promise<DiscountCurveDto[]> | null = null;

const loadCurves = (): Promise<DiscountCurveDto[]> => {
    if (!cache) {
        cache = fetchDiscountCurves().catch((e) => {
            cache = null;
            throw e;
        });
    }
    return cache;
};

/**
 * Scelta di una curva di sconto (yield o spread curve, /discount_curve/r01):
 * solo quelle nella valuta indicata (o senza valuta), piu' quella gia'
 * scelta. Vuota = curva di default del server, indicata nel placeholder.
 */
export const CurveDropdown = ({ value, onChange, currency, placeholder = 'Default curve', disabled }: {
    value: string;
    onChange: (code: string) => void;
    currency?: string;
    placeholder?: string;
    disabled?: boolean;
}) => {
    const [curves, setCurves] = useState<DiscountCurveDto[]>([]);

    useEffect(() => {
        let active = true;
        loadCurves().then((list) => active && setCurves(list)).catch(() => active && setCurves([]));
        return () => {
            active = false;
        };
    }, []);

    const options = curves
        .filter((c) => !currency || !c.currency || c.currency === currency || c.code === value)
        .map((c) => ({
            label: `${c.code}${c.description ? ` — ${c.description}` : ''}${c.kind === 'SPREAD' ? ' (spread)' : ''}`,
            value: c.code,
        }));

    return (
        <Dropdown value={value || null} options={options} filter showClear placeholder={placeholder} disabled={disabled}
            emptyMessage={currency ? `No curves in ${currency}` : 'No curves'} onChange={(e) => onChange(e.value ?? '')} />
    );
};
