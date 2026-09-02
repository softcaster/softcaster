
import { GenericPricingView } from './GenericPricingView';
import { BondPForm } from '../fragments/BondPForm';
import { BondDetails } from '../fragments/BondDetails';
import { fetchXRBMasterDataDto } from '../services/services';

const BondPView = () => (
    <GenericPricingView
        fetchMasterData={fetchXRBMasterDataDto}
        FormComponent={BondPForm}
        FormDetail={BondDetails}
    />
);

export default BondPView;
