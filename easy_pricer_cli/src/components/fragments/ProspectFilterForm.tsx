//import React from 'react';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import type { Counterparty, PositionMasterData } from '../data/schema';

interface ProspectFilter {
    positionMdId: number | null;
    counterpartyId: number | null;
}

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
    onFilterChange,
    onSearch,
    onReset
}: ProspectFilterFormProps) => {
    return (
        <div className="p-fluid grid p-2 gap-3 align-items-end">
            {/* Combo Position */}
            <div className="col-3 field m-0">
                <label className="font-bold block mb-1">Position (MD)</label>
                <Dropdown
                    value={filter.positionMdId}
                    options={positions}
                    optionValue="idPosition"
                    optionLabel="code"
                    onChange={(e) => onFilterChange({ ...filter, positionMdId: e.value })}
                    placeholder="Seleziona Posizione"
                    showClear
                    className="p-dropdown-sm"
                />
            </div>

            {/* Combo Counterparty */}
            <div className="col-3 field m-0">
                <label className="font-bold block mb-1">Counterparty</label>
                <Dropdown
                    value={filter.counterpartyId}
                    options={counterparties}
                    optionValue="idCounterparty"
                    optionLabel="code"
                    onChange={(e) => onFilterChange({ ...filter, counterpartyId: e.value })}
                    placeholder="Seleziona Controparte"
                    showClear
                    className="p-dropdown-sm"
                />
            </div>

            {/* Pulsanti di Azione */}
            <div className="col-3 flex gap-2">
                <Button 
                    label="Cerca" 
                    icon="pi pi-search" 
                    className="p-button-sm p-button-info" 
                    style={{ height: '34px' }} 
                    onClick={onSearch} 
                />
                <Button 
                    label="Reset" 
                    icon="pi pi-filter-slash" 
                    className="p-button-sm p-button-secondary p-button-outlined" 
                    style={{ height: '34px' }} 
                    onClick={onReset} 
                />
            </div>
        </div>
    );
};
