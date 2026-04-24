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
export interface SecurityMasterData {
    idMasterData: number;
    isin: string;
    cfiCode: string;
    fisn: string;
    lei: string;
    issuer: Issuer;
    issueDescription: string;
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
export interface FutureMasterData {
    idMasterData: number;
    isin: string;
    settlementType: SettlementType;
    description: string;
    exchangeContractCode: string;
}

//----------------------------------------------------------------------
// BondFutureMasterData
//----------------------------------------------------------------------
export interface BondFutureMasterData {
    idMasterData: number;
    contractValue: number;
    tickSize: number;
    initialMargin: number;
}

//----------------------------------------------------------------------
// DeliverableBonds
//----------------------------------------------------------------------
export interface DeliverableBonds {
    idDeliverableBonds: number;
    masterData: BondFutureMasterData;
    expirationDate: Date;
    isin: string;
    couponRate: number;
    bondMaturity: Date;
    bondCf: number;
}

//----------------------------------------------------------------------
// FxFutureMasterData
//----------------------------------------------------------------------
export interface FxFutureMasterData {
    idMasterData: number;
    underlying: ForexMasterData;
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
// FinacialTxn
//----------------------------------------------------------------------
export interface FinacialTxn {
    idFinacialTxn: number;
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

