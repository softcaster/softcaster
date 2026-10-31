import { apiGet, apiPost } from './client';
import type {
    AccumulatorDealRequest, AccumulatorDealResponse, AccumulatorDto, AccumulatorPricingResponse,
    GenericMasterDataDto, LoanDealRequest, LoanDto, LoanPricingResponse,
} from './types';

// Mutui
export const priceLoan = (r: LoanDealRequest) => apiPost<LoanPricingResponse>('/otc/loan/price', r);
export const bookLoan = (r: LoanDealRequest) => apiPost<LoanPricingResponse>('/otc/loan/deal', r);
export const fetchLoans = () => apiGet<LoanDto[]>('/otc/loan/r01');
export const valueLoan = (code: string, r: Partial<LoanDealRequest>) =>
    apiPost<LoanPricingResponse>(`/otc/loan/value/${encodeURIComponent(code)}`, r);

// Accumulator
export const priceAccumulator = (r: AccumulatorDealRequest) => apiPost<AccumulatorPricingResponse>('/otc/accumulator/price', r);
export const bookAccumulator = (r: AccumulatorDealRequest) => apiPost<AccumulatorDealResponse>('/otc/accumulator/deal', r);
export const fetchAccumulators = () => apiGet<AccumulatorDto[]>('/otc/accumulator/r01');
/** date (yyyy-MM-dd) vuota = data ufficiale. */
export const valueAccumulator = (code: string, r: Partial<AccumulatorDealRequest>, date?: string) =>
    apiPost<AccumulatorPricingResponse>(`/otc/accumulator/value/${encodeURIComponent(code)}${date ? `?date=${date}` : ''}`, r);

// Anagrafiche per la registrazione
export const fetchCounterparties = () => apiGet<GenericMasterDataDto[]>('/counterparty/r11');
export const fetchPositions = () => apiGet<GenericMasterDataDto[]>('/position_master_data/r11');
export const fetchOfficialDate = () => apiGet<string>('/internal/system/official_date');
