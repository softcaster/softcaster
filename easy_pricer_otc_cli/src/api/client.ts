import axios from 'axios';

// Stesso server REST del client degli strumenti quotati
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
    headers: { 'Content-Type': 'application/json' },
});

export const apiGet = async <T>(url: string): Promise<T> => (await apiClient.get<T>(url)).data;

export const apiPost = async <T>(url: string, body: unknown): Promise<T> => (await apiClient.post<T>(url, body)).data;

/**
 * Messaggio leggibile da un errore REST: i controller OTC rispondono
 * {"Error": "..."} con 400 / 404.
 */
export const errorMessage = (error: unknown): string => {
    if (axios.isAxiosError(error)) {
        const data = error.response?.data as { Error?: string } | string | undefined;
        if (data && typeof data === 'object' && data.Error) {
            return data.Error;
        }
        if (typeof data === 'string' && data.length > 0) {
            return data;
        }
        if (!error.response) {
            return 'Server non raggiungibile (easy_pricer_srv su localhost:8080)';
        }
        return `Errore ${error.response.status}`;
    }
    return error instanceof Error ? error.message : String(error);
};

/** Data locale in formato ISO (yyyy-MM-dd), senza spostamenti di fuso. */
export const isoDate = (date: Date | null | undefined): string | undefined => {
    if (!date) {
        return undefined;
    }
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${m}-${d}`;
};

export const parseDate = (iso: string | null | undefined): Date | null => {
    if (!iso) {
        return null;
    }
    const [y, m, d] = iso.split('-').map(Number);
    return new Date(y, m - 1, d);
};
