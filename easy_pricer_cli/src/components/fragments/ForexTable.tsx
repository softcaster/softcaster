import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import type { FinacialTxn } from '../data/schema';
import  { getSideLabel } from './SideSelector';

interface ForexTableProps {
    data: FinacialTxn[];
    selection: FinacialTxn | null;
    onSelectionChange: (value: FinacialTxn) => void;
}

const sideBodyTemplate = (rowData: FinacialTxn) => {
    const label = getSideLabel(rowData.txnSide); // Usi la funzione centralizzata
    const colorClass = rowData.txnSide === 1 ? 'text-green-600' : 'text-red-600';
    
    return <span className={`font-bold ${colorClass}`}>{label}</span>;
};

export const ForexTable = ({ data, selection, onSelectionChange }: ForexTableProps) => {
    return (
        <DataTable <FinacialTxn[]>
            value={data}
            dataKey="idFinacialTxn"
            selectionMode="single"
            selection={selection}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<FinacialTxn[]>) =>
                onSelectionChange(e.value)}
            stripedRows
            showGridlines
            className="p-datatable-sm shadow-2 w-full"
            tableStyle={{ minWidth: '100%' }}
            scrollable
            scrollHeight="flex"
        >
            <Column field="idFinacialTxn" header="Trade Id" body={(rowData: FinacialTxn) => rowData.idFinacialTxn.toString().padStart(5, '0')} sortable />
            <Column field="txnSide" header="Side" body={sideBodyTemplate} sortable />
            <Column field="price" style={{ textAlign: 'right' }} header="Price" sortable />
            <Column field="quantity" style={{ textAlign: 'right' }} header="Units" sortable />
            <Column field="tradeDate" header="Trade Date" body={(r) => r.tradeDate} />
        </DataTable>
    );
};
