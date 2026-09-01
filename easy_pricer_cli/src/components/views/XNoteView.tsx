import { GenericAssetViewDto } from './GenericAssetViewDto';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchXRNMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const XNoteView = () => (
    <GenericAssetViewDto
        assetClass="XRN"
        fetchMasterData={fetchXRNMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default XNoteView;