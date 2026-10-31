import { Column } from 'primereact/column';
import { DataTable } from 'primereact/datatable';
import type { FinancialTxnDto } from '../api/types';
import { fmt } from './format';

/**
 * Riepilogo delle transazioni, come GenericTxnTable di easy_pricer_cli: la
 * riga selezionata carica la transazione e l'anagrafica collegata.
 */
export const TxnTable = ({ data, selection, onSelectionChange, emptyMessage }: {
    data: FinancialTxnDto[];
    selection: FinancialTxnDto | null;
    onSelectionChange: (value: FinancialTxnDto | null) => void;
    emptyMessage: string;
}) => (
    <div className="flex-1 w-full overflow-hidden flex flex-column">
        <DataTable value={data} dataKey="financialTxnId" selectionMode="single" selection={selection}
            onSelectionChange={(e) => onSelectionChange((e.value as FinancialTxnDto | null) ?? null)}
            rowClassName={(r: FinancialTxnDto) => ({
                'font-italic opacity-60': r.txnStatusCode === 'CANCELLED' || r.txnStatusCode === 'CANCELLED_EXECUTED',
            })}
            stripedRows showGridlines className="p-datatable-sm shadow-2 w-full" tableStyle={{ minWidth: '100%' }}
            scrollable scrollHeight="flex" emptyMessage={emptyMessage}>
            <Column field="financialTxnId" header="Trade Id" sortable
                body={(r: FinancialTxnDto) => (r.financialTxnId ? String(r.financialTxnId).padStart(5, '0') : '-')} />
            <Column field="txnStatusDescription" header="Status" sortable body={(r: FinancialTxnDto) => r.txnStatusDescription || '-'} />
            <Column field="masterDataCode" header="Code" sortable body={(r: FinancialTxnDto) => r.masterDataCode || '-'} />
            <Column field="txnSide" header="Side" body={(r: FinancialTxnDto) => (
                <span className={`font-bold ${r.txnSide === 1 ? 'text-green-600' : 'text-red-600'}`}>{r.txnSide === 1 ? 'Buy' : 'Sell'}</span>
            )} />
            <Column field="price" header="Price" sortable style={{ textAlign: 'right' }} body={(r: FinancialTxnDto) => fmt(r.price, 5)} />
            <Column field="quantity" header="Units" sortable style={{ textAlign: 'right' }} body={(r: FinancialTxnDto) => fmt(r.quantity, 3)} />
            <Column field="counterpartyDesc" header="Counterparty" sortable body={(r: FinancialTxnDto) => r.counterpartyDesc || '-'} />
            <Column field="tradeDate" header="Trade Date" sortable body={(r: FinancialTxnDto) => (r.tradeDate ? String(r.tradeDate).substring(0, 10) : '-')} />
        </DataTable>
    </div>
);
