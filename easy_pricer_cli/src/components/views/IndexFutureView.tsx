import { GenericAssetViewDto } from './GenericAssetViewDto';
import { FxFutureForm } from '../fragments/FxFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchIndexFutureMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

// Deal sui future su indice (FESX): stesso form dei future su cambi e commodity
const IndexFutureView = () => (
    <GenericAssetViewDto
        assetClass="IXF"
        fetchMasterData={fetchIndexFutureMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={FxFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default IndexFutureView;
