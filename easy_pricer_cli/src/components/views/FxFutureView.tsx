import { GenericAssetView } from './GenericAssetView';
import { FxFutureForm } from '../fragments/FxFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchFxFutureMasterData } from '../services/services';
import { DEFAULT_TXN } from '../data/schema';

const FxFutureView = () => (
    <GenericAssetView
        assetClass="FFU"
        fetchMasterData={fetchFxFutureMasterData}
        defaultTxn={DEFAULT_TXN}
        FormComponent={FxFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default FxFutureView;
