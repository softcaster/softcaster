import { GenericAssetViewDto } from './GenericAssetViewDto';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchFRBMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const FltBondView = () => (
    <GenericAssetViewDto
        assetClass="FRB"
        fetchMasterData={fetchFRBMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default FltBondView;
