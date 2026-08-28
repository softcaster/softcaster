import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext';
import type { PricingRequest,YieldCurveDto } from '../services/dto'; // Importa la tua interfaccia base

export function useGenericPricingView<TMaster, TRequest extends PricingRequest, TResponse>(
    fetchMasterData: () => Promise<TMaster[]>,
    fetchYieldCurveDto: () => Promise<YieldCurveDto[]>,
    calculatePricing: (req: TRequest) => Promise<TResponse>,
    defaultRequest: TRequest,
    defaultResponse: TResponse
) {
    const [masterDataList, setMasterDataList] = useState<TMaster[]>([]);
    const [yieldCurveList, setYieldCurveList] = useState<YieldCurveDto[]>([]);
    const [request, setRequest] = useState<TRequest>(defaultRequest);
    const [results, setResults] = useState<TResponse>(defaultResponse);
    const { setAction, showToast } = useActions();

    const handleCalculate = async () => {
        // Ora TypeScript sa con certezza che .isin esiste su TRequest
        if (!request.isin) return; 
        try {
            const data = await calculatePricing(request);
            setResults(data);
            
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
            calculate: handleCalculate,
            new: () => { 
                setRequest(defaultRequest); 
                setResults(defaultResponse); 
            }
        });
    }, [request, calculatePricing, defaultRequest, defaultResponse]);

    const loadAll = async () => {
        try {
            const [mats, ycurves] = await Promise.all([
                fetchMasterData(),
                fetchYieldCurveDto()
            ]);

            setMasterDataList(mats);
            setYieldCurveList(ycurves);
        } catch (err) { 
            console.error(err); 
        }
    };

    useEffect(() => { 
        loadAll(); 
    }, [fetchMasterData]);

    return {
        masterDataList, request, setRequest, results, yieldCurveList
    };
}
