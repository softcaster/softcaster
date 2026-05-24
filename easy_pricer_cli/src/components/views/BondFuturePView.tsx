import { GenericPricingView2 } from './GenericPricingView2';
import { BondFuturePForm } from '../fragments/BondFuturePForm';
import { BondFutureDetails } from '../fragments/BondFutureDetails';
import { fetchBondFutureMasterDataByAssetClass } from '../services/services';
import type { ForwardPricingRequest, ForwardPricingResponse } from '../services/dto';
import {
    DEFAULT_FWD_PRICING_REQUEST, DEFAULT_FWD_PRICING_RESPONSE, calculateFwdBondPricing
} from '../services/dto';

import type {
    BondFutureMasterData
} from '../data/schema';

const BondFuturePView = () => (
    <GenericPricingView2 <BondFutureMasterData, ForwardPricingRequest, ForwardPricingResponse>
        fetchMasterData={fetchBondFutureMasterDataByAssetClass}
        calculatePricing={calculateFwdBondPricing}
        defaultRequest={DEFAULT_FWD_PRICING_REQUEST}
        defaultResponse={DEFAULT_FWD_PRICING_RESPONSE}
        FormComponent={BondFuturePForm}
        FormDetail={BondFutureDetails}
    />
);

export default BondFuturePView;
