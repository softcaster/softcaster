// src/components/views/GenericAssetView.tsx
import React from 'react';
import { usePricingView } from '../hooks/usePricingView';

interface GenericPricingViewProps<TMaster> {
    fetchMasterData: () => Promise<TMaster[]>;
    // Passiamo i componenti come costanti
    FormComponent: React.ComponentType<any>;
}

export function GenericPricingView<TMaster>({
    fetchMasterData,
    FormComponent,
}: GenericPricingViewProps<TMaster>) {

    const {
        masterDataList, request, results, setRequest
    } = usePricingView(fetchMasterData);

    return (
        // Usiamo un contenitore standard invece dello Splitter
        <div className="flex flex-column h-full bg-white overflow-auto">
            <div className="p-3 border-bottom-1 surface-border">
                <FormComponent
                    masterDataList={masterDataList}
                    data={request}         // Passiamo l'oggetto di input
                    results={results}      // Passiamo l'oggetto di output (read-only)
                    onChange={setRequest}  // La funzione per aggiornare lo stato   
                />
            </div>

            {/* Area sottostante vuota che occupa il resto dello spazio */}
            <div className="flex-grow-1 surface-ground p-4">
                {/* Puoi lasciarlo vuoto o mettere un messaggio informativo */}
                <p className="text-400 text-sm italic">Seleziona un contratto per visualizzare il pricing in tempo reale.</p>
            </div>
        </div>
    );
}
