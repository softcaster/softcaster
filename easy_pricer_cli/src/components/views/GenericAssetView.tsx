// src/components/views/GenericAssetView.tsx
import React from 'react';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { useFinancialView } from '../hooks/useFinancialView'; // L'hook creato prima

interface GenericAssetViewProps<TMaster> {
    assetClass: string;
    fetchMasterData: () => Promise<TMaster[]>;
    defaultTxn: any;
    // Passiamo i componenti come costanti
    FormComponent: React.ComponentType<any>;
    TableComponent: React.ComponentType<any>;
}

export function GenericAssetView<TMaster>({
    assetClass,
    fetchMasterData,
    defaultTxn,
    FormComponent,
    TableComponent
}: GenericAssetViewProps<TMaster>) {

    const {
        masterDataList, positionList, counterpartyList,
        trades, selectedTrade, setSelectedTrade
    } = useFinancialView(assetClass, fetchMasterData, defaultTxn);

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">
                <SplitterPanel size={33} minSize={20} className="overflow-auto bg-white p-2">
                    <FormComponent
                        data={selectedTrade}
                        masterDataList={masterDataList} // Nome generico per l'anagrafica
                        positions={positionList}
                        counterparties={counterpartyList}
                        /* 
                           Invece di passare direttamente setSelectedTrade, creiamo un arrow function 
                           che esegue lo scompattamento ({ ...val }). Questo notifica a React che l'oggetto 
                           è cambiato, forzando il ri-rendering istantaneo del campo di testo della descrizione.
                        */
                        onChange={(val: any) => setSelectedTrade(val ? { ...val } : null)}
                    />
                </SplitterPanel>

                <SplitterPanel size={67} minSize={30} className="p-2 flex flex-column overflow-hidden">
                    <TableComponent
                        data={trades}
                        selection={selectedTrade}
                        onSelectionChange={(val: any) => setSelectedTrade(val ?? defaultTxn)}
                    />
                </SplitterPanel>
            </Splitter>
        </div>
    );
}
