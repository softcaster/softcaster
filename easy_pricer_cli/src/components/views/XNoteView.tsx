import { GenericAssetView } from './GenericAssetView';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { findAllSmdByAssetClassXRN } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const XNoteView = () => (
    <GenericAssetView
        assetClass="XRN"
        fetchMasterData={findAllSmdByAssetClassXRN}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default XNoteView;