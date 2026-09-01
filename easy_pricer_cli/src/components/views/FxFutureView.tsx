import { GenericAssetViewDto } from './GenericAssetViewDto';
import { FxFutureForm } from '../fragments/FxFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchFxFutureMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const FxFutureView = () => (
    <GenericAssetViewDto
        assetClass="FFU"
        fetchMasterData={fetchFxFutureMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={FxFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default FxFutureView;
