//----------------------------------------------------------------------
// Daycount
//----------------------------------------------------------------------
export interface Daycount {
    idDaycount: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// Frequency
//----------------------------------------------------------------------
export interface Frequency {
    idFrequency: number;
    code: string;
    description: string;
    yearFraction: number;
}

//----------------------------------------------------------------------
// Form
//----------------------------------------------------------------------
export interface Form {
    idForm: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// RollConvention
//----------------------------------------------------------------------
export interface RollConvention {
    idRollConvention: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// TypeOfInterest
//----------------------------------------------------------------------
export interface TypeOfInterest {
    idTypeOfInterest: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// AccrualScheduleType
//----------------------------------------------------------------------
export interface AccrualScheduleType {
    idAccrualScheduleType: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// MarketSegment
//----------------------------------------------------------------------
export interface MarketSegment {
    idMarketSegment: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// AmortizationSchedule
//----------------------------------------------------------------------
export interface AmortizationSchedule {
    idAmortizationSchedule: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// Calendar
//----------------------------------------------------------------------
export interface Calendar {
    idCalendar: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// Holiday
//----------------------------------------------------------------------
export interface Holiday {
    idHoliday: number;
    calendar: Calendar;
    holidayDay: number;
    holidayMonth: number;
    description: string;
}

//----------------------------------------------------------------------
// Currency
//----------------------------------------------------------------------
export interface Currency {
    idCurrency: number;
    isoCode: string;
    currencyNumericCode: number;
    description: string;
    minorUnit: number;
    systemCurr: number;
    physicalCurr: number;
    calendar: Calendar;
    businessDays: number;
    daycount: Daycount;
}

//----------------------------------------------------------------------
// Country
//----------------------------------------------------------------------
export interface Country {
    idCountry: number;
    countryName: string;
    officialStateName: string;
    alfa_2Code: string;
    alfa_3Code: string;
    countryNumericCode: number;
    sovereign: string;
    subdivisionCodeLinks: string;
    internetCcTld: string;
    currency: Currency;
    calendar: Calendar;
}

//----------------------------------------------------------------------
// Issuer
//----------------------------------------------------------------------
export interface Issuer {
    idIssuer: number;
    shortIssuerName: string;
    longIssuerName: string;
    country: Country;
}

//----------------------------------------------------------------------
// SuperClass
//----------------------------------------------------------------------
export interface SuperClass {
    idSuperClass: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// AssetClass
//----------------------------------------------------------------------
export interface AssetClass {
    idAssetClass: number;
    superClass: SuperClass;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// MasterData
//----------------------------------------------------------------------
export interface MasterData {
    idMasterData: number;
    code: string;
    description: string;
    currency: Currency;
    calendar: Calendar;
    issueDate: Date;
    maturityDate: Date;
    typeOfInterest: TypeOfInterest;
    form: Form;
    daycount: Daycount;
    frequency: Frequency;
    rollConvention: RollConvention;
    accrualScheduleType: AccrualScheduleType;
    interestRate: number;
    issuePrice: number;
    redempionPrice: number;
    businessDays: number;
    assetClass: AssetClass;
    amortizationSchedule: AmortizationSchedule;
}

//----------------------------------------------------------------------
// LoanMasterData
//----------------------------------------------------------------------
export interface LoanMasterData {
    idMasterData: number;
    description: string;
    processingFees: number;
    managementFees: number;
    incidentalExpenses: number;
    defaultInterest: number;
    latePaymentFee: number;
    underwritingFee: number;
    insurancePremium: number;
    taxCharges: number;
}

//----------------------------------------------------------------------
// SecurityMasterData
//----------------------------------------------------------------------
export interface SecurityMasterData extends MasterData {
    idMasterData: number;
    isin: string;
    cfiCode: string;
    fisn: string;
    lei: string;
    issuer: Issuer;
    nominalValue: number;
    firstCouponRate: number;
    firstCouponPaymentDate: Date;
}

//----------------------------------------------------------------------
// CashFlowItem
//----------------------------------------------------------------------
export interface CashFlowItem {
    idCashFlowItem: number;
    masterData: BondFutureMasterData;
    startDate: Date;
    endDate: Date;
    interest: number;
    amount: number;
}

//----------------------------------------------------------------------
// CashFlowReset
//----------------------------------------------------------------------
export interface CashFlowReset {
}

//----------------------------------------------------------------------
// SettlementType
//----------------------------------------------------------------------
export interface SettlementType {
    idSettlementType: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// FutureMasterData
//----------------------------------------------------------------------
export interface FutureMasterData extends MasterData {
    isin: string;
    settlementType: SettlementType;
    exchangeContractCode: string;
}

//----------------------------------------------------------------------
// BondFutureMasterData
//----------------------------------------------------------------------
export interface BondFutureMasterData extends FutureMasterData {
    contractValue: number;
    tickSize: number;
    initialMargin: number;
    deliverables: DeliverableBonds[];
}

//----------------------------------------------------------------------
// DeliverableBonds
//----------------------------------------------------------------------
export interface DeliverableBonds {
    idDeliverableBonds: number;
    masterData: number; // id bond future
    expirationDate: Date;
    isin: string; // isin sottostante
    couponRate: number;
    bondMaturity: Date;
    bondCf: number;
}

//----------------------------------------------------------------------
// FxFutureMasterData
//----------------------------------------------------------------------
export interface FxFutureMasterData extends FutureMasterData {
    idMasterData: number;
    underlying: ForexMasterData;
    contractValue: number;
    tickSize: number;
    initialMargin: number;
    maintenanceMargin: number;
}

//----------------------------------------------------------------------
// CmdFutureMasterData
//----------------------------------------------------------------------
export interface CmdFutureMasterData extends FutureMasterData {
    idMasterData: number;
    commodityType: string;
    contractValue: number;
    tickSize: number;
    initialMargin: number;
    maintenanceMargin: number;
}

//----------------------------------------------------------------------
// MmFutureMasterData
//----------------------------------------------------------------------
export interface MmFutureMasterData {
    idMasterData: number;
    underlying: ForexMasterData;
    contractValue: number;
    tickSize: number;
    initialMargin: number;
    maintenanceMargin: number;
}

//----------------------------------------------------------------------
// InstrumentQuote
//----------------------------------------------------------------------
export interface InstrumentQuote {
    idInstrumentQuote: number;
    masterData: BondFutureMasterData;
    code: string;
    bid: number;
    ask: number;
    provider: string;
}

//----------------------------------------------------------------------
// InstrumentQuoteHist
//----------------------------------------------------------------------
export interface InstrumentQuoteHist {
    idInstrumentQuoteHist: number;
    instrumentQuote: InstrumentQuote;
    masterData: BondFutureMasterData;
    code: string;
    bid: number;
    ask: number;
    updateDate: Date;
}

//----------------------------------------------------------------------
// ForexMasterData
//----------------------------------------------------------------------
export interface ForexMasterData extends MasterData {
    bcy: Currency;
    ccy: Currency;
    bcyIrc: string;
    ccyIrc: string;
}

//----------------------------------------------------------------------
// CounterpartyType
//----------------------------------------------------------------------
export interface CounterpartyType {
    idCounterpartyType: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// Counterparty
//----------------------------------------------------------------------
export interface Counterparty {
    idCounterparty: number;
    ctpType: CounterpartyType;
    country: Country;
    code: string;
    description: string;
    leiCode: string;
}

//----------------------------------------------------------------------
// PortfolioMasterData
//----------------------------------------------------------------------
export interface PortfolioMasterData {
    idPortfolio: number;
    currency: Currency;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// PositionMasterData
//----------------------------------------------------------------------
export interface PositionMasterData {
    idPosition: number;
    currency: Currency;
    code: string;
    description: string;
    portfolio: PortfolioMasterData;
}

//----------------------------------------------------------------------
// PositionDetail
//----------------------------------------------------------------------
export interface PositionDetail {
    idPositionDetail: number;
    positionMd: PositionMasterData;
    masterData: BondFutureMasterData;
    realizedPnl: number;
    unrealizedPnl: number;
    avgPrice: number;
    marketValue: number;
    netQuantity: number;
}

//----------------------------------------------------------------------
// TxnStatus
//----------------------------------------------------------------------
export interface TxnStatus {
    idTxnStatus: number;
    code: string;
    description: string;
}

//----------------------------------------------------------------------
// FinancialTxn
//----------------------------------------------------------------------
export interface FinancialTxn {
    idFinancialTxn: number;
    counterparty: Counterparty;
    positionMd: PositionMasterData;
    masterData: MasterData;
    txnStatus: TxnStatus;
    txnSide: number;
    description: string;
    tradeDate: Date | null;
    settlement: Date | null;
    quantity: number;
    price: number;
    version: number; 
}

//----------------------------------------------------------------------
// YieldCurve
//----------------------------------------------------------------------
export interface YieldCurve {
    idYieldCurve: number;
    code: string;
    description: string;
    currency: Currency;
    calendar: Calendar;
    compounding: number;
}

//----------------------------------------------------------------------
// YieldCurveItem
//----------------------------------------------------------------------
export interface YieldCurveItem {
    idYieldCurveItem: number;
    yieldCurve: YieldCurve;
    ric: string;
    offsetType: number;
    offsetValue: number;
    bid: number;
    ask: number;
}

export const DEFAULT_TXN: FinancialTxn = {
    idFinancialTxn: 0,
    counterparty: {} as Counterparty,
    positionMd: {} as PositionMasterData,
    masterData: { code: '' } as ForexMasterData, // Inizializzato come ForexMD
    txnStatus: {} as TxnStatus,
    txnSide: 1,
    description: '',
    tradeDate: new Date(),
    settlement: new Date(),
    quantity: 0,
    price: 0,
    version: 0
};

export const createDefaultTxn = (): FinancialTxn => ({
    idFinancialTxn: 0,
    counterparty: { idCounterparty: 0, description: '' } as Counterparty,
    positionMd: { idPosition: 0, code: '' } as PositionMasterData,
    masterData: { idMasterData: 0, code: '' } as any,
    txnStatus: { idTxnStatus: 0, description: '' } as TxnStatus, txnSide: 1,
    description: '',
    tradeDate: new Date(),
    settlement: new Date(),
    quantity: 0,
    price: 0,
    version: 0
});