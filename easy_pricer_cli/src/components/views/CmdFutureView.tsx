import { GenericAssetView } from './GenericAssetView';
import { FxFutureForm } from '../fragments/FxFutureForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchCmdFutureMasterData } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

const CmdFutureView = () => (
    <GenericAssetView
        assetClass="CFU"
        fetchMasterData={fetchCmdFutureMasterData}
        defaultTxn={DEFAULT_TXN_DTO}
        // Riutilizzo componenti Fx
        FormComponent={FxFutureForm}
        TableComponent={GenericTxnTable}
    />
);

export default CmdFutureView;