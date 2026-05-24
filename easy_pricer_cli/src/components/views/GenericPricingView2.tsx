import React from 'react';
import { useGenericPricingView } from '../hooks/useGenericPricingView';
import type { PricingRequest, PricingResponse } from '../services/dto';

interface GenericPricingViewProps2<TMaster extends { code: string }, TRequest, TResponse> {
    fetchMasterData: () => Promise<TMaster[]>;
    calculatePricing: (req: TRequest) => Promise<TResponse>;
    defaultRequest: TRequest;
    defaultResponse: TResponse;
    FormComponent: React.ComponentType<any>;
    FormDetail: React.ComponentType<any>;
}

export function GenericPricingView2<
    TMaster extends { code: string },
    TRequest extends PricingRequest, // Vincolo sulla interfaccia base
    TResponse extends PricingResponse
>({
    fetchMasterData,
    calculatePricing,
    defaultRequest,
    defaultResponse,
    FormComponent,
    FormDetail,
}: GenericPricingViewProps2<TMaster, TRequest, TResponse>) {

    const {
        masterDataList, request, results, setRequest
    } = useGenericPricingView<TMaster, TRequest, TResponse>(
        fetchMasterData,
        calculatePricing,
        defaultRequest,
        defaultResponse
    );

    // Funziona perfettamente perché sia TMaster ha .code sia request ha .isin
    const selectedRecord = masterDataList.find(m => m.code === request.isin);

    return (
        <div className="flex flex-column h-full bg-white overflow-auto">
            <div className="p-3 border-bottom-1 surface-border">
                <FormComponent
                    masterDataList={masterDataList}
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
