import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { ForexTrade } from '../data/fxtrade';
import type { DataTableSelectionSingleChangeEvent } from 'primereact/datatable';

interface ForexTableProps {
    data: ForexTrade[];
    selection: ForexTrade | null;
    onSelectionChange: (value: ForexTrade) => void;
}

export const ForexTable = ({ data, selection, onSelectionChange }: ForexTableProps) => {
    return (
        <DataTable <ForexTrade[]>
            value={data}
            selectionMode="single"
            selection={selection}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<ForexTrade[]>) =>
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
