import { GenericAssetView } from './GenericAssetView';
import { BondFutureForm } from '../fragments/BondFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchBondFutureMasterData } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const BondFutureView = () => (
    <GenericAssetView
        assetClass="BFU"
        fetchMasterData={fetchBondFutureMasterData}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondFutureView;