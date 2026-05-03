import axios from 'axios';
import type { AxiosResponse } from 'axios';

// Create an instance of axios with some default configuration
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080/',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Gestione globale degli errori
apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        // Qui puoi gestire errori 401 (unauthorized) o mostrare un Toast
        console.error("API Error:", error.response?.data || error.message);
        return Promise.reject(error);
    }
);

// Define a generic API function
export const apiRequest = async <T>(url: string, method: 'GET' | 'POST' | 'PUT' | 'DELETE', data?: any): Promise<T> => {
    const response: AxiosResponse<T> = await apiClient({
        method,
        url,
        data,
    });

    return response.data;
};

export const downloadFinancialTxnCsv = async (assetClass: string): Promise<Blob> => {
    const response = await axios.get('http://localhost:8080/financial_txn/export/' + assetClass, {
        responseType: 'blob'       
    });
    return response.data;
};
