import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext';
import { useSystemDate } from '../../context/SystemDateContext';
import type { PricingRequest, YieldCurveDto } from '../services/dto'; // Importa la tua interfaccia base

export function useGenericPricingView<TMaster, TRequest extends PricingRequest, TResponse>(
    fetchMasterData: () => Promise<TMaster[]>,
    fetchYieldCurveDto: () => Promise<YieldCurveDto[]>,
    calculatePricing: (req: TRequest) => Promise<TResponse>,
    defaultRequest: TRequest,
    defaultResponse: TResponse
) {
    const [masterDataList, setMasterDataList] = useState<TMaster[]>([]);
    const [yieldCurveList, setYieldCurveList] = useState<YieldCurveDto[]>([]);
    const [results, setResults] = useState<TResponse>(defaultResponse);
    const { setAction, showToast } = useActions();
    const { businessDate, loading: dateLoading } = useSystemDate();

    // Nuovo helper dinamico che pulisce le date su QUALSIASI oggetto DTO passato
    // Rende la funzione generica aggiungendo <R extends PricingRequest>
    const applySystemDateToDto = <R extends PricingRequest>(dto: R): R => {
        // Creiamo una copia superficiale per preservare l'immutabilità dello stato di React
        //const updatedDto = { ...dto };

        if (businessDate && !dateLoading) {
            const [year, month, day] = businessDate.split('-').map(Number);
            const officialSystemDate = new Date(year, month - 1, day);
            dto.referenceDate = officialSystemDate;
        }
        return dto;
    };
    const [request, setRequest] = useState<TRequest>(applySystemDateToDto(defaultRequest));

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
