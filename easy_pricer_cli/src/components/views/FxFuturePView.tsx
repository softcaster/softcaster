import { GenericPricingView2 } from './GenericPricingView2';
import { FxFuturePForm } from '../fragments/FxFuturePForm';
import { BondFutureDetails } from '../fragments/BondFutureDetails';
import { fetchFxFutureMasterDataDto, fetchYieldCurveDto } from '../services/services';
import type { ForwardPricingRequest, ForwardPricingResponse } from '../services/dto';
import {
    DEFAULT_FWD_PRICING_REQUEST, DEFAULT_FWD_PRICING_RESPONSE, calculateFwdFxPricing
} from '../services/dto';

const FxFuturePView = () => (
    <GenericPricingView2 <ForwardPricingRequest, ForwardPricingResponse>
        fetchMasterData={fetchFxFutureMasterDataDto}
        fetchYieldCurveDto={fetchYieldCurveDto}
        calculatePricing={calculateFwdFxPricing}
        defaultRequest={DEFAULT_FWD_PRICING_REQUEST}
        defaultResponse={DEFAULT_FWD_PRICING_RESPONSE}  
        FormComponent={FxFuturePForm}
        FormDetail={BondFutureDetails}
    />
);

export default FxFuturePView;
