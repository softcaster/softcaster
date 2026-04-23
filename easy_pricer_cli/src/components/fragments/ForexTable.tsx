import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import type { FinacialTxn} from '../data/schema';

interface ForexTableProps {
    data: FinacialTxn[];
    selection: FinacialTxn | null;
    onSelectionChange: (value: FinacialTxn) => void;
}

export const ForexTable = ({ data, selection, onSelectionChange }: ForexTableProps) => {
    return (
        <DataTable <FinacialTxn[]>
            value={data}
            selectionMode="single"
            selection={selection}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<FinacialTxn[]>) =>
                onSelectionChange(e.value)}
            dataKey="id"
            stripedRows
            showGridlines
            className="p-datatable-sm shadow-2 w-full"
            tableStyle={{ minWidth: '100%' }}
            scrollable
            scrollHeight="flex"
        >
            <Column field="reference" header="Ref" sortable />
            <Column field="price" style={{ textAlign: 'right' }} header="Price" sortable />
            <Column field="units" style={{ textAlign: 'right' }} header="Units" sortable />
            <Column field="valueDate" header="Date" body={(r) => r.valueDate?.toLocaleDateString()} />
        </DataTable>
    );
};
