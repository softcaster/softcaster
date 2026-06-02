import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import type { DataTableSelectionSingleChangeEvent } from 'primereact/datatable';
import type { FinancialTxnDto } from '../services/dto';
import { getSideLabel } from './SideSelector';
import { formatPrice, formatUnits } from '../../utils/formatters';

interface GenericTxnTableProps {
    data: FinancialTxnDto[];
    selection: FinancialTxnDto | null;
    onSelectionChange: (value: FinancialTxnDto | null) => void;
}

const sideBodyTemplate = (rowData: FinancialTxnDto) => {
    const label = getSideLabel(rowData.txnSide); 
    const colorClass = rowData.txnSide === 1 ? 'text-green-600' : 'text-red-600';

    return <span className={`font-bold ${colorClass}`}>{label}</span>;
};

export const GenericTxnTable = ({ data, selection, onSelectionChange }: GenericTxnTableProps) => {
    return (
        <DataTable
            value={data}
            rowClassName={(rowData: FinancialTxnDto) => ({
                'font-italic opacity-60': (rowData.txnStatus?.code === 'CANCELLED' || rowData.txnStatus?.code === 'CANCELLED_EXECUTED')
            })}
            // 1. Allineato alla chiave primaria definita nella export interface
            dataKey="financialTxnId" 
            selectionMode="single"
            selection={selection}
            onSelectionChange={(e: DataTableSelectionSingleChangeEvent<any>) => onSelectionChange(e.value)}
            stripedRows
            showGridlines
            className="p-datatable-sm shadow-2 w-full"
            tableStyle={{ minWidth: '100%' }}
            scrollable
            scrollHeight="flex"
        >
            {/* 2. Gestione sicura del padStart nel caso in cui financialTxnId sia temporaneamente null o 0 */}
            <Column 
                field="financialTxnId" 
                header="Trade Id" 
                body={(rowData: FinancialTxnDto) => {
                    if (!rowData.financialTxnId) return '-';
                    return String(rowData.financialTxnId).padStart(5, '0');
                }} 
                sortable 
            />
            
            <Column field="txnStatus" header="Status" body={(rowData: FinancialTxnDto) => rowData.txnStatus?.description || '-'} sortable />
            
            <Column field="masterDataCode" header="Code" body={(rowData: FinancialTxnDto) => rowData.masterDataCode || '-'} sortable />
            
            <Column field="txnSide" header="Side" body={sideBodyTemplate} />
            
            <Column field="price" style={{ textAlign: 'right' }} header="Price" body={(rowData: FinancialTxnDto) => formatPrice(rowData.price)} sortable />
            
            <Column field="quantity" style={{ textAlign: 'right' }} header="Units" body={(rowData: FinancialTxnDto) => formatUnits(rowData.quantity)} sortable />
            
            {/* 3.  Allineato a counterpartyDesc come definito nelDTO */}
            <Column field="counterpartyDesc" header="Counterparty" body={(rowData: FinancialTxnDto) => rowData.counterpartyDesc || '-'} sortable />
            
            {/* 4. Forza la conversione a stringa o testo leggibile per evitare il crash del tipo Date/String di Jackson */}
            <Column 
                field="tradeDate" 
                header="Trade Date" 
                body={(rowData: FinancialTxnDto) => {
                    if (!rowData.tradeDate) return '-';
                    // Se a runtime è un oggetto Date reale usa toISOString, altrimenti stampa la stringa così com'è
                    return rowData.tradeDate instanceof Date ? rowData.tradeDate.toLocaleDateString() : String(rowData.tradeDate);
                }} 
                sortable 
            />
        </DataTable>
    );
};
