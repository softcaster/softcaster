
import { GenericPricingView } from './GenericPricingView';
import { BondPForm } from '../fragments/BondPForm';
import { BondDetails } from '../fragments/BondDetails';
import { fetchXRBMasterDataDto2 } from '../services/services';

const BondPView = () => (
    <GenericPricingView
        fetchMasterData={fetchXRBMasterDataDto2}
        FormComponent={BondPForm}
        FormDetail={BondDetails}
    />
);

export default BondPView;
