import { GenericAssetViewDto } from './GenericAssetViewDto';
import { BondFutureForm } from '../fragments/BondFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchBondFutureMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const BondFutureView = () => (
    <GenericAssetViewDto
        assetClass="BFU"
        fetchMasterData={fetchBondFutureMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondFutureView;