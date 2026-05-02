import { GenericAssetView } from './GenericAssetView';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { findAllSmdByAssetClassXRB } from '../services/services';
import { DEFAULT_TXN } from '../data/schema';

const BondView = () => (
    <GenericAssetView
        assetClass="XRB"
        fetchMasterData={findAllSmdByAssetClassXRB}
        defaultTxn={DEFAULT_TXN}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondView;
