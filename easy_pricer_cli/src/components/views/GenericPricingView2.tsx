import React from 'react';
import { useGenericPricingView } from '../hooks/useGenericPricingView';
import type { GenericMasterDataDto, PricingRequest, PricingResponse, YieldCurveDto } from '../services/dto';

interface GenericPricingViewProps2<TRequest, TResponse> {
    fetchMasterData: () => Promise<GenericMasterDataDto[]>;
    fetchYieldCurveDto: () => Promise<YieldCurveDto[]>;
    calculatePricing: (req: TRequest) => Promise<TResponse>;
    defaultRequest: TRequest;
    defaultResponse: TResponse;
    FormComponent: React.ComponentType<any>;
    FormDetail: React.ComponentType<any>;
}

export function GenericPricingView2<
    TRequest extends PricingRequest, // Vincolo sulla interfaccia base
    TResponse extends PricingResponse
>({
    fetchMasterData,
    fetchYieldCurveDto,
    calculatePricing,
    defaultRequest,
    defaultResponse,
    FormComponent,
    FormDetail,
}: GenericPricingViewProps2<TRequest, TResponse>) {

    const {
        masterDataList, request, setRequest, results, yieldCurveList
    } = useGenericPricingView<TRequest, TResponse>(
        fetchMasterData,
        fetchYieldCurveDto,
        calculatePricing,
        defaultRequest,
        defaultResponse
    );

    // Funziona perfettamente perché sia GenericMasterDataDto ha .code sia request ha .isin
    const selectedRecord = masterDataList.find(m => m.code === request.isin);

    return (
        <div className="flex flex-column h-full bg-white overflow-auto">
            <div className="p-3 border-bottom-1 surface-border">
                <FormComponent
                    masterDataList={masterDataList}
                    yieldCurveList={yieldCurveList}
                    data={request}
                    results={results}
                    onChange={setRequest}
                />
            </div>

            <div className="flex-grow-1 surface-ground p-4 overflow-y-auto">
                <FormDetail data={selectedRecord} />
            </div>
        </div>
    );
}
