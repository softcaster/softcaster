const number = (digits: number) => new Intl.NumberFormat('en-US', { minimumFractionDigits: digits, maximumFractionDigits: digits });

export const fmt = (value: number | null | undefined, digits = 2): string =>
    value === null || value === undefined || Number.isNaN(value) ? '—' : number(digits).format(value);

export const pct = (value: number | null | undefined, digits = 4): string =>
    value === null || value === undefined ? '—' : `${number(digits).format(value)}%`;

export const tone = (value: number | null | undefined): 'pos' | 'neg' | undefined =>
    value === null || value === undefined || value === 0 ? undefined : value > 0 ? 'pos' : 'neg';

export const fmtDate = (iso: string | null | undefined): string => {
    if (!iso) {
        return '—';
    }
    const [y, m, d] = iso.split('-');
    return `${d}/${m}/${y}`;
};

/** Percentuale inserita dall'utente (3,2) in decimale (0,032); null se vuota. */
export const fromPercent = (value: number | null | undefined): number | null =>
    value === null || value === undefined ? null : value / 100;
