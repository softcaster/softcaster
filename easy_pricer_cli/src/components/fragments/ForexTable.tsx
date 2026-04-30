import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import type { FinancialTxn } from '../data/schema';
import  { getSideLabel } from './SideSelector';
import { formatPrice, formatUnits } from '../../utils/formatters';

interface ForexTableProps {
    data: FinancialTxn[];
    selection: FinancialTxn | null;
    onSelectionChange: (value: FinancialTxn) => void;
}

const sideBodyTemplate = (rowData: FinancialTxn) => {
    const label = getSideLabel(rowData.txnSide); // Usi la funzione centralizzata
    const colorClass = rowData.txnSide === 1 ? 'text-green-600' : 'text-red-600';
    
    return <span className={`font-bold ${colorClass}`}>{label}</span>;
};

export const ForexTable = ({ data, selection, onSelectionChange }: ForexTableProps) => {
    return (
        <DataTable <FinancialTxn[]>
            value={data}
            dataKey="idFinancialTxn"
            selectionMode="single"
            selection={selection}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<FinancialTxn[]>) =>
                onSelectionChange(e.value)}
            stripedRows
            showGridlines
            className="p-datatable-sm shadow-2 w-full"
            tableStyle={{ minWidth: '100%' }}
            scrollable
            scrollHeight="flex"
        >
            <Column field="idFinancialTxn" header="Trade Id" body={(rowData: FinancialTxn) => rowData.idFinancialTxn.toString().padStart(5, '0')} sortable />
            <Column field="code" header="Code" body={(rowData: FinancialTxn) => rowData.masterData.code} sortable />
            <Column field="txnSide" header="Side" body={sideBodyTemplate} />
            <Column field="price" style={{ textAlign: 'right' }} header="Price" body={(rowData) => formatPrice(rowData.price)} sortable />
            <Column field="quantity" style={{ textAlign: 'right' }} header="Units" body={(rowData) => formatUnits(rowData.quantity)} sortable />
            <Column field="counterparty" header="Counterparty" body={(r) => r.counterparty?.description || '-'} sortField="counterparty.description" sortable/>
            <Column field="tradeDate" header="Trade Date" body={(r) => r.tradeDate} sortable/>
        </DataTable>
    );
};
