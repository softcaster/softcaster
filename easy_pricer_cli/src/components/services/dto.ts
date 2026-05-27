import axios from 'axios';
import type { TxnStatus } from '../data/schema';


const BASE_URL = 'http://localhost:8080/api/v1';

export interface PricingRequest {
    isin: string;             // Il codice del bond (es: IT000...)
    referencePrice: number;   // Il prezzo inserito
    referenceDate: Date;      // La data selezionata
}

export interface BondPricingRequest extends PricingRequest {
    yieldCurve: string;
    fullCalc: boolean;
}

export interface ForwardPricingRequest extends PricingRequest {
    domesticRate: number;
    foreignRate: number;
    maturityDate: Date;
}

export interface PricingResponse {
}

export interface BondPricingResponse extends PricingResponse {
    accruedInterest: number;
    yieldToMaturity: number;
    macaulayDuration: number;
    modifiedDuration: number;
    convexity: number;
    presentValue: number;
    yieldToMaturityPV: number;
}

export interface ForwardPricingResponse extends PricingResponse {
    theoreticalPrice: number;
    ctd: string;
}

export const DEFAULT_BOND_PRICING_REQUEST: BondPricingRequest = {
    isin: '',
    referencePrice: 100.00,
    referenceDate: new Date(),
    yieldCurve: '',
    fullCalc: false
};

export const DEFAULT_FWD_PRICING_REQUEST: ForwardPricingRequest = {
    isin: '',
    referencePrice: 100.00,
    referenceDate: new Date(),
    domesticRate: 0.01,
    foreignRate: 0.01,
    maturityDate: new Date()
};

export const DEFAULT_BOND_PRICING_RESPONSE: BondPricingResponse = {
    accruedInterest: 0,
    yieldToMaturity: 0,
    modifiedDuration: 0,
    macaulayDuration: 0,
    convexity: 0,
    presentValue: 0,
    yieldToMaturityPV: 0
};

export const DEFAULT_FWD_PRICING_RESPONSE: ForwardPricingResponse = {
    theoreticalPrice: 0,
    ctd: ""
};

export const calculateBondPricing = async (request: BondPricingRequest): Promise<BondPricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/bond`, request);
    return response.data;
};

export const calculateFwdBondPricing = async (request: ForwardPricingRequest): Promise<ForwardPricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/future`, request);
    return response.data;
};


// Rappresenta il DTO principale FinancialTxnDto
export interface FinancialTxnDto {
    financialTxnId: number;
    description: string;
    counterpartyId: number | null;
    counterpartyCode: string | null;
    counterpartyDesc: string | null;
    positionMdId: number | null;
    positionMdCode: string | null;
    masterDataId: number | null;
    masterDataCode: string | null;
    masterDataDesc: string | null;
    txnStatus: TxnStatus | null; // Mappato come oggetto reale coerentemente con il backend
    refId: number;
    txnSide: number;
    tradeDate: Date;
    settlement: Date;
    quantity: number;
    price: number;
};

export const DEFAULT_TXN_DTO: FinancialTxnDto = {
    financialTxnId: 0,
    description: '',
    counterpartyId: 0,
    counterpartyCode: '',
    counterpartyDesc: '',
    positionMdId: 0,
    positionMdCode: '',
    masterDataId: 0,
    masterDataCode: '',
    masterDataDesc: '',
    txnStatus: {} as TxnStatus,
    refId: 0,
    txnSide: 1,
    tradeDate: new Date,
    settlement: new Date,
    quantity: 0,
    price: 0
};

export const createDefaultTxnDto = (): FinancialTxnDto => ({
    financialTxnId: 0,
    description: '',
    counterpartyId: 0,
    counterpartyCode: '',
    counterpartyDesc: '',
    positionMdId: 0,
    positionMdCode: '',
    masterDataId: 0,
    masterDataCode: '',
    masterDataDesc: '',
    txnStatus: { idTxnStatus: 0, description: '' } as TxnStatus,
    refId: 0,
    txnSide: 1,
    tradeDate: new Date,
    settlement: new Date,
    quantity: 0,
    price: 0
});
