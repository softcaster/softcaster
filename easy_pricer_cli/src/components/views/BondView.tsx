import { GenericAssetView } from './GenericAssetView';
import { BondForm } from '../fragments/BondForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { findAllSmdByAssetClassXRB } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const BondView = () => (
    <GenericAssetView
        assetClass="XRB"
        fetchMasterData={findAllSmdByAssetClassXRB}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={BondForm}
        TableComponent={GenericTxnTable}
    />
);

export default BondView;
