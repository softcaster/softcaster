import axios from 'axios';
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

export interface BondPricingResponse {
    accruedInterest: number;
    yieldToMaturity: number;
    macaulayDuration: number;
    modifiedDuration: number;
    convexity: number;
    presentValue: number;
    yieldToMaturityPV: number;
}

// src/data/dto.ts

export const DEFAULT_BOND_PRICING_REQUEST: BondPricingRequest = {
    isin: '',
    referencePrice: 100.00,
    referenceDate: new Date(),
    yieldCurve: '',
    fullCalc: false
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

export const calculateBondPricing = async (request: BondPricingRequest): Promise<BondPricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/bond`, request);
    return response.data;
};

