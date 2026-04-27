/**
 * Formatta un prezzo con decimali fissi (es: 1,17350)
 */
export const formatPrice = (value: number | undefined | null, decimals: number = 5): string => {
    if (value === undefined || value === null) return '-';
    return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals
    }).format(value);
};

/**
 * Formatta le quantità con separatore delle migliaia (es: 10.000)
 */
export const formatUnits = (value: number | undefined | null, decimals: number = 3): string => {
    if (value === undefined || value === null) return '-';
    return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: decimals,
        maximumFractionDigits: decimals
    }).format(value);
};

/**
 * Formatta le date in modo consistente
 */
export const formatDate = (value: Date | string | undefined | null): string => {
    if (!value) return '-';
    const date = typeof value === 'string' ? new Date(value) : value;
    return new Intl.NumberFormat('it-IT').format(date.getTime());
    // Oppure più semplicemente:
    return date.toLocaleDateString('it-IT');
};
