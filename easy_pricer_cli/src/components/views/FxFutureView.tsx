import { GenericAssetView } from './GenericAssetView';
import { FxFutureForm } from '../fragments/FxFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchFxFutureMasterData } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const FxFutureView = () => (
    <GenericAssetView
        assetClass="FFU"
        fetchMasterData={fetchFxFutureMasterData}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={FxFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default FxFutureView;
