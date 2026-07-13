import { GenericProspectView } from './GenericProspectView';
import { ProspectFilterForm } from '../fragments/ProspectFilterForm';
import { AccountingProspectTable } from '../fragments/AccountingProspectTable';

// funzione reale che fa la POST tramite apiRequest
import { fetchBalanceByPositionDetail } from '../services/services';

const AccountingProspectView = () => (
    <GenericProspectView
        fetchProspectData={fetchBalanceByPositionDetail} 
        FilterComponent={ProspectFilterForm}
        TableComponent={AccountingProspectTable}
    />
);
export default AccountingProspectView;
