import { GenericAssetView } from './GenericAssetView';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { findAllSmdByAssetClassXRN } from '../services/services';
import { DEFAULT_TXN } from '../data/schema';

const XNoteView = () => (
    <GenericAssetView
        assetClass="XRN"
        fetchMasterData={findAllSmdByAssetClassXRN}
        defaultTxn={DEFAULT_TXN}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default XNoteView;