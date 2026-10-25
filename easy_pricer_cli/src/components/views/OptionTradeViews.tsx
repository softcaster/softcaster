import { GenericAssetViewDto } from './GenericAssetViewDto';
import { OptionForm } from '../fragments/OptionForm';
import { GenericTxnTable } from '../fragments/GenericTxnTable';
import { fetchMasterDataDto } from '../services/services';
import { DEFAULT_TXN_DTO } from '../services/dto';

// Deal sulle serie di opzioni di una famiglia (asset class della testata)
const OptionTradeView = ({ assetClass }: { assetClass: string }) => (
    <GenericAssetViewDto
        assetClass={assetClass}
        fetchMasterData={() => fetchMasterDataDto(assetClass)}
        defaultTxn={DEFAULT_TXN_DTO}
        FormComponent={OptionForm}
        TableComponent={GenericTxnTable}
    />
);

export const FxOptionView = () => <OptionTradeView assetClass="FXO" />;
export const FxFutureOptionView = () => <OptionTradeView assetClass="FFO" />;
export const BondOptionView = () => <OptionTradeView assetClass="BDO" />;
export const BondFutureOptionView = () => <OptionTradeView assetClass="BFO" />;
