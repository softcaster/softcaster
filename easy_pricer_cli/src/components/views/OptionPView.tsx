import { GenericPricingView2 } from './GenericPricingView2';
import { OptionPForm } from '../fragments/OptionPForm';
import { OptionDetails } from '../fragments/OptionDetails';
import { fetchOptionMasterDataDto, fetchYieldCurveDto } from '../services/services';
import {
    DEFAULT_OPTION_PRICING_REQUEST, DEFAULT_PRICING_RESPONSE, calculateOptionPricing
} from '../services/dto';

const OptionPView = () => (
    <GenericPricingView2
        fetchMasterData={fetchOptionMasterDataDto}
        fetchYieldCurveDto={fetchYieldCurveDto}
        calculatePricing={calculateOptionPricing}
        defaultRequest={DEFAULT_OPTION_PRICING_REQUEST}
        defaultResponse={DEFAULT_PRICING_RESPONSE}
        FormComponent={OptionPForm}
        FormDetail={OptionDetails}
    />
);

export default OptionPView;
