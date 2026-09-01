// src/components/ForexView.tsx
import { GenericAssetViewDto } from './GenericAssetViewDto';
import { ForexForm } from '../fragments/ForexForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchForexMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const ForexView = () => (
    <GenericAssetViewDto
        assetClass="FSP"
        fetchMasterData={fetchForexMasterDataDto}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={ForexForm}
        TableComponent={GenericTxnTable}
    />
);

export default ForexView;


