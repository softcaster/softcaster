// src/hooks/usePricingView.ts
import { useState, useEffect } from 'react';
import type { BondPricingRequest, BondPricingResponse } from '../services/dto';
import { DEFAULT_BOND_PRICING_REQUEST, DEFAULT_BOND_PRICING_RESPONSE } from '../services/dto';
import { useActions } from '../../context/ActionContext';
import { calculateBondPricing } from '../services/dto';

export function usePricingView<TMaster>(
    fetchMasterData: () => Promise<TMaster[]>
) {
    const [masterDataList, setMasterDataList] = useState<TMaster[]>([]);
    const [request, setRequest] = useState<BondPricingRequest>(DEFAULT_BOND_PRICING_REQUEST);
    const [results, setResults] = useState<BondPricingResponse>(DEFAULT_BOND_PRICING_RESPONSE);
    const { setAction } = useActions();
    const { showToast } = useActions(); // Recupero showToast

    const handleCalculate = async () => {
        if (!request.isin) return;
        try {
            const data = await calculateBondPricing(request);
            setResults(data);
            // Notifica di successo
            showToast({
                severity: 'success',
                summary: 'Pricing Updated',
                detail: `Calculated for ${request.isin}`,
                life: 3000
            });
        } catch (err) {
            console.error("Errore calcolo:", err);
        }
    };

    useEffect(() => {
        setAction({
            // Aggiungiamo uno nuovo 'calculate'
            calculate: handleCalculate,
            new: () => { setRequest(DEFAULT_BOND_PRICING_REQUEST); setResults(DEFAULT_BOND_PRICING_RESPONSE); }
        });
    }, [request]); // Si ri-registra quando la request cambia


    const loadAll = async () => {
        try {
            const [mats] = await Promise.all([
                fetchMasterData(),
            ]);
            setMasterDataList(mats);
        } catch (err) { console.error(err); }
    };

    useEffect(() => { loadAll(); }, []);

    return {
        masterDataList, request, setRequest, results
    };
}
