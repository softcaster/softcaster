// src/components/views/GenericAssetView.tsx
import React from 'react';
import { usePricingView } from '../hooks/usePricingView';
import {BondDetails} from '../fragments/BondDetails';

// Usando extends, garantisci a TypeScript che qualunque cosa sia , 
// avrà sicuramente un campo code
interface GenericPricingViewProps<TMaster extends { code: string }> {
    fetchMasterData: () => Promise<TMaster[]>;
    // Passiamo i componenti come costanti
    FormComponent: React.ComponentType<any>;
}

export function GenericPricingView<TMaster extends { code: string }>({
    fetchMasterData,
    FormComponent,
}: GenericPricingViewProps<TMaster>) {

    const {
        masterDataList, request, results, setRequest
    } = usePricingView(fetchMasterData);

    const selectedBond = masterDataList.find(m => m.code === request.isin);

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

            {/* Area sottostante vuota che occupa il resto dello spazio. Assicuro che l'area sia scrollabile */}
            <div className="flex-grow-1 surface-ground p-4 overflow-y-auto">
                {/* Passiamo il masterData selezionato per popolare le card */}
                <BondDetails data={selectedBond} />
            </div>
        </div>
    );
}
