import React from 'react';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { useProspectView } from '../hooks/useProspectView';
import type {
    ProspectFilter
} from '../services/dto';

interface GenericProspectViewProps {
    fetchProspectData: (filter: ProspectFilter) => Promise<any[]>;
    FilterComponent: React.ComponentType<any>;
    TableComponent: React.ComponentType<any>;
}

export function GenericProspectView({
    fetchProspectData,
    FilterComponent,
    TableComponent
}: GenericProspectViewProps) {

    const {
        positionList,
        counterpartyList,
        assetClassList,
        prospectData,
        filter,
        setFilter,
        loading,
        handleSearch,
        handleExport,
        handleReset
    } = useProspectView(fetchProspectData);

    return (
        <div style={{ height: 'calc(100vh - 45px)', padding: '1rem', boxSizing: 'border-box' }} className="flex flex-column">
            <Splitter layout="vertical" style={{ height: '100%' }} className="border-none">

                {/* Pannello superiore per il Form di Filtro */}
                <SplitterPanel size={25} minSize={15} className="overflow-auto bg-white p-2">
                    <FilterComponent
                        filter={filter}
                        positions={positionList}
                        counterparties={counterpartyList}
                        assetClasses={assetClassList}
                        onFilterChange={setFilter}
                        onSearch={handleSearch}
                        onReset={handleReset}
                        onExport={handleExport}
                    />
                </SplitterPanel>

                {/* Pannello inferiore per la DataTable */}
                <SplitterPanel size={75} minSize={30} className="p-2 flex flex-column overflow-hidden">
                    <TableComponent
                        data={prospectData}
                        loading={loading}
                    />
                </SplitterPanel>

            </Splitter>
        </div>
    );
}
