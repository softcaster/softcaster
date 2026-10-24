import axios from 'axios';

const BASE_URL = 'http://localhost:8080/api/v1';

// Richiesta di pricing unica (PricingRequest in mds_core): ogni endpoint legge
// solo i campi che gli servono.
export interface PricingRequest {
    isin: string;              // codice o ISIN dello strumento
    referencePrice?: number;   // bond: prezzo secco; bond future: prezzo quotato; FX future: spot
    referenceDate: Date;       // inviata come "yyyy-MM-dd"
    // bond
    yieldCurve?: string;
    fullCalc?: boolean;
    referenceRate?: number;    // floating rate bond
    // forward / future
    domesticRate?: number;     // bond future: tasso repo se domesticRCurve e' vuota
    domesticRCurve?: string;
    foreignRate?: number;
    foreignRCurve?: string;
    maturityDate?: Date;
}

// Risposta di pricing unica (PricingResponse in mds_core): il server invia
// solo i campi calcolati. Valori per 100 di nominale, tassi in decimale.
export interface PricingResponse {
    // bond
    accruedInterest?: number;
    yieldToMaturity?: number;
    macaulayDuration?: number;
    modifiedDuration?: number;
    convexity?: number;
    presentValue?: number;
    yieldToMaturityPV?: number;
    dv01?: number;
    discountMargin?: number;
    shortBondYield?: number;
    marginDuration?: number;
    // forward / future
    theoreticalPrice?: number;
    forwardPoints?: number;
    termRepoRate?: number;
    ctd?: string;
    ctdYield?: number;
    ctdModifiedDuration?: number;
    ctdDv01?: number;
    ctdNetBasis?: number;
    ctdImpliedRepo?: number;
    futureDv01?: number;
}

export const DEFAULT_BOND_PRICING_REQUEST: PricingRequest = {
    isin: '',
    referencePrice: 100.00,
    referenceDate: new Date(),
    yieldCurve: '',
    fullCalc: false
};

export const DEFAULT_FWD_PRICING_REQUEST: PricingRequest = {
    isin: '',
    referencePrice: 1.00,
    referenceDate: new Date(),
    domesticRate: 0.01,
    domesticRCurve: '',
    foreignRate: 0.01,
    foreignRCurve: ''
};

export const DEFAULT_PRICING_RESPONSE: PricingResponse = {};

// Data locale in "yyyy-MM-dd": JSON.stringify di una Date la converte in UTC
// e a mezzanotte ora italiana darebbe il giorno precedente.
const toIsoDate = (d?: Date | null): string | undefined => {
    if (!d) return undefined;
    const date = new Date(d);
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${date.getFullYear()}-${mm}-${dd}`;
};

const toPayload = (request: PricingRequest) => ({
    ...request,
    referenceDate: toIsoDate(request.referenceDate),
    maturityDate: toIsoDate(request.maturityDate)
});

export const calculateBondPricing = async (request: PricingRequest): Promise<PricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/bond`, toPayload(request));
    return response.data;
};

export const calculateFwdBondPricing = async (request: PricingRequest): Promise<PricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/future`, toPayload(request));
    return response.data;
};
export const calculateFwdFxPricing = async (request: PricingRequest): Promise<PricingResponse> => {
    const response = await axios.post(`${BASE_URL}/pricing/fx-future`, toPayload(request));
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
    txnStatusId: number | null;
    txnStatusCode: string | null;
    txnStatusDescription: string | null;
    refId: number;
    txnSide: number;
    tradeDate: Date;
    settlement: Date;
    quantity: number;
    price: number;
    fxRate: number;
    txnAcctPhase: number | null
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
    txnStatusId: 0,
    txnStatusCode: '',
    txnStatusDescription: '',
    refId: 0,
    txnSide: 1,
    tradeDate: new Date,
    settlement: new Date,
    quantity: 0,
    price: 0,
    fxRate: 1,
    txnAcctPhase: 1
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
    txnStatusId: 0,
    txnStatusCode: '',
    txnStatusDescription: '',
    refId: 0,
    txnSide: 1,
    tradeDate: new Date,
    settlement: new Date,
    quantity: 0,
    price: 0,
    fxRate: 1,
    txnAcctPhase: 1
});

export interface YieldCurveDto {
    yieldCurveId: number;
    code: string;
    description: string;
}

export const DEFAULT_YCURVE_DTO: YieldCurveDto = {
    yieldCurveId: 0,
    code: '',
    description: ''
}

export interface ProspectFilter {
    positionId: number | null;
    counterpartyId: number | null;
    assetClassId: number | null;
}

export interface PositionProspectDto {
    positionCode: number;
    assetCode: string;
    assetDescription: string;
    counterpartyCode: string;
    totalQuantity: number;
    averagePrice: number;
    marketPrice: number;
    marketValue: number;
    realizedPnL: number;
    unrealizedPnL: number;
}

export interface AccountDetailsBalanceDto {
    positionDetail: number;
    accountId: number;
    code: string;
    description: string;
    ccyCode: string;
    totalDebit: number;
    totalCredit: number;
}

export interface GenericMasterDataDto {
    genericMasterDataId: number;
    code: string;
    description: string;
    classIdentity: string;
}

export interface MasterDataDto extends GenericMasterDataDto {

    assetClass: string;
    currency: string;
    issuePrice: number;
    redempionPrice: number;
    interestRate: number;
    issueDate: Date;
    maturityDate: Date;
}

export interface SecurityMasterDataDto extends MasterDataDto {

    shortIssuerName: string;
    longIssuerName: string;
    frequency: string;
    country: string;
    firstCouponPaymentDate: Date;
    firstCouponRate: number;
}

