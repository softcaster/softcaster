//import React from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { formatPrice, formatUnits } from '../../utils/formatters';

export interface PositionProspectDto {
    positionId: number;
    positionCode: string;
    counterpartyCode: string;
    totalQuantity: number;
    averagePrice: number;
    marketPrice: number;
    marketValue: number;
    realizedPnL: number;
    unrealizedPnL: number;
}

interface PositionProspectTableProps {
    data: PositionProspectDto[];
    loading: boolean;
}

export const PositionProspectTable = ({ data, loading }: PositionProspectTableProps) => {
    
    const pnlBodyTemplate = (rowData: PositionProspectDto, field: 'realizedPnL' | 'unrealizedPnL') => {
        const value = rowData[field];
        const colorClass = value >= 0 ? 'text-green-600 font-bold' : 'text-red-600 font-bold';
        return <span className={colorClass}>{formatPrice(value)}</span>;
    };

    return (
        <div className="flex-1 w-full overflow-hidden flex flex-column">
            <DataTable
                value={data}
                loading={loading}
                scrollable
                scrollHeight="flex"
                className="p-datatable-sm"
                dataKey="positionId"
            >
                <Column field="positionCode" header="Position" style={{ fontWeight: 'bold' }} />
                <Column field="counterpartyCode" header="Counterparty" />
                <Column field="totalQuantity" header="Quantity" body={(r) => formatUnits(r.totalQuantity)} />
                <Column field="averagePrice" header="Avg Price" body={(r) => formatPrice(r.averagePrice)} />
                <Column field="marketPrice" header="Mkt Price" body={(r) => formatPrice(r.marketPrice)} />
                <Column field="marketValue" header="Mkt Value" body={(r) => formatPrice(r.marketValue)} />
                <Column field="realizedPnL" header="Realized P&L" body={(r) => pnlBodyTemplate(r, 'realizedPnL')} />
                <Column field="unrealizedPnL" header="Unrealized P&L" body={(r) => pnlBodyTemplate(r, 'unrealizedPnL')} />
            </DataTable>
        </div>
    );
};
