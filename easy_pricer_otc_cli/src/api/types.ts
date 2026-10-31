// Tipi dei servizi REST OTC di easy_pricer_srv (date in formato yyyy-MM-dd)

export interface Option {
    label: string;
    value: string;
}

export interface GenericMasterDataDto {
    genericMasterDataId: number;
    code: string;
    description: string;
}

// ---------------------------------------------------------------------------
// Mutui
// ---------------------------------------------------------------------------

export interface LoanFeeInput {
    type: string;      // PROCESSING, UNDERWRITING, TAX, INCIDENTAL, MANAGEMENT, INSURANCE
    basis: string;     // AMOUNT, PERCENT (punti: 0.25 = 0,25%)
    timing: string;    // UPFRONT, PER_INSTALLMENT
    value: number | null;
}

export interface LoanDealRequest {
    rateType: string;
    currency: string;
    principalAmount: number | null;
    annualRate: number | null;          // punti percentuali
    disbursementDate?: string;
    firstPaymentDate?: string;
    maturityDate?: string;
    frequency: string;
    amortization: string;
    daycount: string;
    rollConvention: string;
    preAmortizationPeriods: number;
    description?: string;
    fees: LoanFeeInput[];
    defaultInterest?: number | null;
    latePaymentFee?: number | null;
    spread?: number | null;             // decimale
    floor?: number | null;
    cap?: number | null;
    indexCode?: string;
    tradeDate?: string;
    side?: string;
    counterpartyId?: number | null;
    positionMdId?: number | null;
    valuationDate?: string;
    discountCurve?: string;
    discountRate?: number | null;       // decimale continuo
    creditSpread?: number | null;       // decimale continuo
}

export interface LoanInstallment {
    number: number;
    startDate: string;
    endDate: string;
    paymentDate: string;
    principal: number;
    interest: number;
    fees: number;
    total: number;
    outstanding: number;
}

export interface LoanPricingResponse {
    code: string | null;
    valuationDate: string;
    discountSource: string;
    principal: number;
    annualRate: number;
    installment: number;
    outstandingPrincipal: number;
    accruedInterest: number;
    nextPaymentDate: string | null;
    nextPaymentAmount: number;
    fairValue: number;
    fairValuePercent: number;
    yield: number;
    modifiedDuration: number;
    dv01: number;
    taeg: number;
    upfrontFees: number;
    totalInterest: number;
    totalFees: number;
    schedule: LoanInstallment[];
}

export interface LoanDto {
    masterDataId: number;
    code: string;
    description: string;
    assetClass: string;
    currency: string;
    principalAmount: number;
    annualRate: number;
    disbursementDate: string;
    firstPaymentDate: string | null;
    maturityDate: string;
    frequency: string;
    amortization: string;
    daycount: string;
    preAmortizationPeriods: number;
    installments: number;
    fees: { type: string; basis: string; timing: string; value: number }[];
    spread: number | null;
    floor: number | null;
    cap: number | null;
}

// ---------------------------------------------------------------------------
// Accumulator
// ---------------------------------------------------------------------------

export interface AccumulatorDealRequest {
    underlyingCode: string;
    accumulatorType: string;
    settlementType: string;
    settlementFrequency: string;
    strike: number | null;
    koBarrier: number | null;
    koObservation: string;
    kiBarrier?: number | null;
    kiEffect?: string | null;
    leverage: number | null;
    dailyQuantity?: number | null;
    maxQuantity?: number | null;
    firstFixingDate?: string;
    lastFixingDate?: string;
    guaranteedPeriods: number;
    guaranteeType?: string | null;
    koAccrual: boolean;
    description?: string;
    tradeDate?: string;
    side?: string;
    counterpartyId?: number | null;
    positionMdId?: number | null;
    premium?: number | null;
    premiumPercent?: number | null;
    spot?: number | null;
    volatility?: number | null;         // decimale; vuota = superficie
    paths?: number | null;
}

export interface AccumulatorPricingResponse {
    code: string | null;
    model: string;
    valuationDate: string;
    spot: number;
    volatility: number;
    volatilitySource: string;
    value: number;
    standardError: number;
    pendingValue: number;
    unitValue: number;
    maxQuantity: number;
    expectedQuantity: number;
    knockOutProbability: number;
    totalFixings: number;
    futureFixings: number;
    delta: number;
    gamma: number;
    vega: number;
    paths: number;
}

export interface AccumulatorDto {
    masterDataId: number;
    code: string;
    description: string;
    assetClass: string;
    underlyingCode: string;
    currency: string;
    accumulatorType: string;
    settlementType: string;
    settlementFrequency: string;
    strike: number;
    koBarrier: number;
    koObservation: string;
    kiBarrier: number | null;
    kiEffect: string | null;
    leverage: number;
    dailyQuantity: number;
    guaranteedPeriods: number;
    guaranteeType: string;
    guaranteedFixings: number;
    koAccrual: boolean;
    issueDate: string;
    firstFixingDate: string;
    lastFixingDate: string;
    maturityDate: string;
    koDate: string | null;
    kiDate: string | null;
    totalFixings: number;
    maxQuantity: number;
}

export interface AccumulatorDealResponse {
    contract: AccumulatorDto;
    side: string;
    quantity: number;
    premium: number;
    pricing: AccumulatorPricingResponse | null;
}
