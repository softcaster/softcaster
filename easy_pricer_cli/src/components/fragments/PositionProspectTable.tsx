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
                <Column field="assetCode" header="Code" />
                <Column field="totalQuantity" header="Quantity" style={{ textAlign: 'right' }} body={(r) => formatUnits(r.totalQuantity)} />
                <Column field="averagePrice" header="Avg Price" style={{ textAlign: 'right' }} body={(r) => formatPrice(r.averagePrice)} />
                <Column field="marketPrice" header="Mkt Price" style={{ textAlign: 'right' }} body={(r) => formatPrice(r.marketPrice)} />
                <Column field="marketValue" header="Mkt Value" style={{ textAlign: 'right' }} body={(r) => formatPrice(r.marketValue)} />
                <Column field="realizedPnL" header="Realized P&L" style={{ textAlign: 'right' }} body={(r) => formatUnits(r.realizedPnL)} />
                <Column field="unrealizedPnL" header="Unrealized P&L" style={{ textAlign: 'right' }} body={(r) => formatUnits(r.unrealizedPnL)} />
            </DataTable>
        </div>
    );
};
