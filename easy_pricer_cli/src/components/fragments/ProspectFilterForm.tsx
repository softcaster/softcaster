//import React from 'react';
import type { Counterparty, PositionMasterData } from '../data/schema';
import { CounterpartyField, PositionField } from './FormFields';

import type {
    ProspectFilter
} from '../services/dto';

interface ProspectFilterFormProps {
    filter: ProspectFilter;
    positions: PositionMasterData[];
    counterparties: Counterparty[];
    onFilterChange: (filter: ProspectFilter) => void;
    onSearch: () => void;
    onReset: () => void;
}

export const ProspectFilterForm = ({
    filter,
    positions,
    counterparties,
    onFilterChange
}: ProspectFilterFormProps) => {

    const currentPosition = positions.find(p => p.idPosition === filter.positionId) || null;
    const currentCounterparty = counterparties.find(c => c.idCounterparty === filter.counterpartyId) || null;

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
                    }} />
            </div>
        </div>
    );
};

