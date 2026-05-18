import { GenericAssetView } from './GenericAssetView';
import { BondFutureForm } from '../fragments/BondFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchBondFutureMasterData } from '../services/services';
import { DEFAULT_TXN } from '../data/schema';

const BondFutureView = () => (
    <GenericAssetView
        assetClass="FFU"
        fetchMasterData={fetchBondFutureMasterData}
        defaultTxn={DEFAULT_TXN}
        FormComponent={BondFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondFutureView;