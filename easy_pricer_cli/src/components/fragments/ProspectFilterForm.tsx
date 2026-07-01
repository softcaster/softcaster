//import React from 'react';
import type { AssetClass, Counterparty, PositionMasterData } from '../data/schema';
import { CounterpartyField, PositionField, AssetClassField } from './FormFields';
import { Button } from 'primereact/button';
import type {
    ProspectFilter
} from '../services/dto';

interface ProspectFilterFormProps {
    filter: ProspectFilter;
    positions: PositionMasterData[];
    counterparties: Counterparty[];
    assetClasses: AssetClass[];
    onFilterChange: (filter: ProspectFilter) => void;
    onSearch: () => void;
    onReset: () => void;
    onExport: () => void;
}

export const ProspectFilterForm = ({
    filter,
    positions,
    counterparties,
    assetClasses,
    onFilterChange,
    onReset
}: ProspectFilterFormProps) => {

    const currentPosition = positions.find(p => p.idPosition === filter.positionId) || null;
    const currentCounterparty = counterparties.find(c => c.idCounterparty === filter.counterpartyId) || null;
    const currentAssetClass = assetClasses.find(c => c.idAssetClass === filter.assetClassId) || null;

    return (
        <div className="bg-white p-2 border-bottom-1 surface-border w-full">
            {/* Forza la riga in flex-layout orizzontale nativo */}
            <div className="grid p-fluid flex flex-row gap-2 align-items-end">

                    <PositionField
                        value={currentPosition} // <-- Passa l'oggetto PositionMasterData intero
                        options={positions}
                        onChange={(selectedPosition: any) => {
                            // Quando cambia, estre l'ID numerico per aggiornare lo stato del filtro
                            onFilterChange({
                                ...filter,
                                positionId: selectedPosition ? selectedPosition.idPosition : null
                            });
                        }}
                    />
                    <CounterpartyField
                        value={currentCounterparty} // <-- Passa l'oggetto Counterparty intero
                        options={counterparties}
                        onChange={(selectedCounterparty: any) => {
                            // Quando cambia, estre l'ID numerico per aggiornare lo stato del filtro
                            onFilterChange({
                                ...filter,
                                counterpartyId: selectedCounterparty ? selectedCounterparty.idCounterparty : null
                            });
                        }}
                    />
                    <AssetClassField
                        label="Asset Class"
                        value={currentAssetClass} // <-- Passa l'oggetto Counterparty intero
                        options={assetClasses}
                        onChange={(selectedAssetClasse: any) => {
                            // Quando cambia, estre l'ID numerico per aggiornare lo stato del filtro
                            onFilterChange({
                                ...filter,
                                assetClassId: selectedAssetClasse ? selectedAssetClasse.idAssetClass : null
                            });
                        }}
                    />

                <div className="col-12 md:col-2">
                    <Button
                        type="button"
                        icon="pi pi-filter-slash"
                        label="Reset"
                        className="p-button-outlined p-button-secondary font-bold w-full"
                        style={{ height: '34px' }} // Pareggia l'altezza standard delle combo PrimeReact
                        onClick={() => onReset()}
                        tooltip="Clear all filters"
                        tooltipOptions={{ position: 'top' }}
                    />
                </div>
            </div>
        </div>
    );
};

