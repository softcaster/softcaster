import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { formatPrice, formatUnits } from '../../utils/formatters';
import type {AccountDetailsBalanceDto} from '../services/dto'

interface AccountingProspectTableProps {
    data: AccountDetailsBalanceDto[];
    loading: boolean;
}

export const AccountingProspectTable = ({ data, loading }: AccountingProspectTableProps) => {

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
                <Column field="accountId" header="Account Id" style={{ fontWeight: 'bold' }} />
                <Column field="code" header="Code" />
                <Column field="description" header="Description" />
                <Column field="totalDebit" header="Debit" style={{ textAlign: 'right' }} body={(r) => formatUnits(r.totalDebit)} />
                <Column field="totalCredit" header="Credit" style={{ textAlign: 'right' }} body={(r) => formatPrice(r.totalCredit)} />
            </DataTable>
        </div>
    );
};
