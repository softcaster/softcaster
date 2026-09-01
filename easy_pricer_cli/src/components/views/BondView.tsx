import { GenericAssetViewDto } from './GenericAssetViewDto';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchXRBMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const BondView = () => (
    <GenericAssetViewDto
        assetClass="XRB"
        fetchMasterData={fetchXRBMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondView;
