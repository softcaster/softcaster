// src/components/ForexView.tsx
import { GenericAssetView } from './GenericAssetView';
import { ForexForm } from '../fragments/ForexForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchForexMasterData } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const ForexView = () => (
    <GenericAssetView
        assetClass="FSP"
        fetchMasterData={fetchForexMasterData}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={ForexForm}
        TableComponent={GenericTxnTable}
    />
);

export default ForexView;


