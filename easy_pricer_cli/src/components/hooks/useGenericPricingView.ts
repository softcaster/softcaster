import { useState, useEffect } from 'react';
import { useActions } from '../../context/ActionContext';
import { useSystemDate } from '../../context/SystemDateContext';
import type { PricingRequest, PricingResponse, YieldCurveDto, GenericMasterDataDto } from '../services/dto';

export function useGenericPricingView(
    fetchMasterData: () => Promise<GenericMasterDataDto[]>,
    fetchYieldCurveDto: () => Promise<YieldCurveDto[]>,
    calculatePricing: (req: PricingRequest) => Promise<PricingResponse>,
    defaultRequest: PricingRequest,
    defaultResponse: PricingResponse
) {
    const [masterDataList, setMasterDataList] = useState<GenericMasterDataDto[]>([]);
    const [yieldCurveList, setYieldCurveList] = useState<YieldCurveDto[]>([]);
    const [results, setResults] = useState<PricingResponse>(defaultResponse);
    const { setAction, showToast } = useActions();
    const { businessDate, loading: dateLoading } = useSystemDate();

    // Imposta la data di sistema come data di riferimento della request
    const applySystemDateToDto = (dto: PricingRequest): PricingRequest => {
        // Creiamo una copia superficiale per preservare l'immutabilità dello stato di React
        //const updatedDto = { ...dto };

        if (businessDate && !dateLoading) {
            const [year, month, day] = businessDate.split('-').map(Number);
            const officialSystemDate = new Date(year, month - 1, day);
            dto.referenceDate = officialSystemDate;
        }
        return dto;
    };
    const [request, setRequest] = useState<PricingRequest>(applySystemDateToDto(defaultRequest));

    const handleCalculate = async () => {
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
