
import { GenericPricingView } from './GenericPricingView';
import { BondPForm } from '../fragments/BondPForm';
import { findAllSmdByAssetClassXRB } from '../services/services';

const BondPView = () => (
    <GenericPricingView
        fetchMasterData={findAllSmdByAssetClassXRB}
        FormComponent={BondPForm}
    />
);

export default BondPView;
