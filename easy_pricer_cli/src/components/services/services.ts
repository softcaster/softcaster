import { apiRequest } from './apiclient';
import type {
    Daycount, Frequency, Form, RollConvention, TypeOfInterest, AccrualScheduleType, MarketSegment, AmortizationSchedule,
    Calendar, Holiday, Currency, Country, Issuer, SuperClass, AssetClass, MasterData, LoanMasterData, SecurityMasterData, CashFlowItem,
    CashFlowReset, SettlementType, FutureMasterData, BondFutureMasterData, DeliverableBonds, FxFutureMasterData, MmFutureMasterData,
    InstrumentQuote, InstrumentQuoteHist, ForexMasterData, CounterpartyType, Counterparty, PortfolioMasterData, PositionMasterData,
    PositionDetail, TxnStatus, FinancialTxn, YieldCurve, YieldCurveItem
} from '../data/schema'
import type {
    FinancialTxnDto, PositionProspectDto
} from '../services/dto';

import type {
    ProspectFilter
} from '../services/dto';

export const fetchDaycount = async (): Promise<Daycount[]> => {
    try {
        return await apiRequest<Daycount[]>('/daycount/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch daycount:', error);
        return [];
    }
};

export const fetchDaycountById = async (id: number): Promise<Daycount | null> => {
    try {
        return await apiRequest<Daycount>('/daycount/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch daycount:', error);
        return null;
    }
};

export const saveDaycount = async (daycount: Daycount): Promise<Daycount | null> => {
    try {
        return await apiRequest<Daycount>('/daycount', 'POST', daycount);
    } catch (error) {
        console.error('Failed to save daycount:', error);
        return null;
    }
};

export const deleteDaycount = async (id: number): Promise<Daycount | null> => {
    try {
        return await apiRequest<Daycount>('/daycount/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete daycount:', error);
        return null;
    }
};

export const fetchFrequency = async (): Promise<Frequency[]> => {
    try {
        return await apiRequest<Frequency[]>('/frequency/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch frequency:', error);
        return [];
    }
};

export const fetchFrequencyById = async (id: number): Promise<Frequency | null> => {
    try {
        return await apiRequest<Frequency>('/frequency/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch frequency:', error);
        return null;
    }
};

export const saveFrequency = async (frequency: Frequency): Promise<Frequency | null> => {
    try {
        return await apiRequest<Frequency>('/frequency', 'POST', frequency);
    } catch (error) {
        console.error('Failed to save frequency:', error);
        return null;
    }
};

export const deleteFrequency = async (id: number): Promise<Frequency | null> => {
    try {
        return await apiRequest<Frequency>('/frequency/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete frequency:', error);
        return null;
    }
};


export const fetchForm = async (): Promise<Form[]> => {
    try {
        return await apiRequest<Form[]>('/form/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch form:', error);
        return [];
    }
};

export const fetchFormById = async (id: number): Promise<Form | null> => {
    try {
        return await apiRequest<Form>('/form/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch form:', error);
        return null;
    }
};

export const saveForm = async (form: Form): Promise<Form | null> => {
    try {
        return await apiRequest<Form>('/form', 'POST', form);
    } catch (error) {
        console.error('Failed to save form:', error);
        return null;
    }
};

export const deleteForm = async (id: number): Promise<Form | null> => {
    try {
        return await apiRequest<Form>('/form/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete form:', error);
        return null;
    }
};


export const fetchRollConvention = async (): Promise<RollConvention[]> => {
    try {
        return await apiRequest<RollConvention[]>('/roll_convention/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch roll_convention:', error);
        return [];
    }
};

export const fetchRollConventionById = async (id: number): Promise<RollConvention | null> => {
    try {
        return await apiRequest<RollConvention>('/roll_convention/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch roll_convention:', error);
        return null;
    }
};

export const saveRollConvention = async (roll_convention: RollConvention): Promise<RollConvention | null> => {
    try {
        return await apiRequest<RollConvention>('/roll_convention', 'POST', roll_convention);
    } catch (error) {
        console.error('Failed to save roll_convention:', error);
        return null;
    }
};

export const deleteRollConvention = async (id: number): Promise<RollConvention | null> => {
    try {
        return await apiRequest<RollConvention>('/roll_convention/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete roll_convention:', error);
        return null;
    }
};


export const fetchTypeOfInterest = async (): Promise<TypeOfInterest[]> => {
    try {
        return await apiRequest<TypeOfInterest[]>('/type_of_interest/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch type_of_interest:', error);
        return [];
    }
};

export const fetchTypeOfInterestById = async (id: number): Promise<TypeOfInterest | null> => {
    try {
        return await apiRequest<TypeOfInterest>('/type_of_interest/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch type_of_interest:', error);
        return null;
    }
};

export const saveTypeOfInterest = async (type_of_interest: TypeOfInterest): Promise<TypeOfInterest | null> => {
    try {
        return await apiRequest<TypeOfInterest>('/type_of_interest', 'POST', type_of_interest);
    } catch (error) {
        console.error('Failed to save type_of_interest:', error);
        return null;
    }
};

export const deleteTypeOfInterest = async (id: number): Promise<TypeOfInterest | null> => {
    try {
        return await apiRequest<TypeOfInterest>('/type_of_interest/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete type_of_interest:', error);
        return null;
    }
};


export const fetchAccrualScheduleType = async (): Promise<AccrualScheduleType[]> => {
    try {
        return await apiRequest<AccrualScheduleType[]>('/accrual_schedule_type/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch accrual_schedule_type:', error);
        return [];
    }
};

export const fetchAccrualScheduleTypeById = async (id: number): Promise<AccrualScheduleType | null> => {
    try {
        return await apiRequest<AccrualScheduleType>('/accrual_schedule_type/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch accrual_schedule_type:', error);
        return null;
    }
};

export const saveAccrualScheduleType = async (accrual_schedule_type: AccrualScheduleType): Promise<AccrualScheduleType | null> => {
    try {
        return await apiRequest<AccrualScheduleType>('/accrual_schedule_type', 'POST', accrual_schedule_type);
    } catch (error) {
        console.error('Failed to save accrual_schedule_type:', error);
        return null;
    }
};

export const deleteAccrualScheduleType = async (id: number): Promise<AccrualScheduleType | null> => {
    try {
        return await apiRequest<AccrualScheduleType>('/accrual_schedule_type/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete accrual_schedule_type:', error);
        return null;
    }
};


export const fetchMarketSegment = async (): Promise<MarketSegment[]> => {
    try {
        return await apiRequest<MarketSegment[]>('/market_segment/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch market_segment:', error);
        return [];
    }
};

export const fetchMarketSegmentById = async (id: number): Promise<MarketSegment | null> => {
    try {
        return await apiRequest<MarketSegment>('/market_segment/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch market_segment:', error);
        return null;
    }
};

export const saveMarketSegment = async (market_segment: MarketSegment): Promise<MarketSegment | null> => {
    try {
        return await apiRequest<MarketSegment>('/market_segment', 'POST', market_segment);
    } catch (error) {
        console.error('Failed to save market_segment:', error);
        return null;
    }
};

export const deleteMarketSegment = async (id: number): Promise<MarketSegment | null> => {
    try {
        return await apiRequest<MarketSegment>('/market_segment/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete market_segment:', error);
        return null;
    }
};


export const fetchAmortizationSchedule = async (): Promise<AmortizationSchedule[]> => {
    try {
        return await apiRequest<AmortizationSchedule[]>('/amortization_schedule/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch amortization_schedule:', error);
        return [];
    }
};

export const fetchAmortizationScheduleById = async (id: number): Promise<AmortizationSchedule | null> => {
    try {
        return await apiRequest<AmortizationSchedule>('/amortization_schedule/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch amortization_schedule:', error);
        return null;
    }
};

export const saveAmortizationSchedule = async (amortization_schedule: AmortizationSchedule): Promise<AmortizationSchedule | null> => {
    try {
        return await apiRequest<AmortizationSchedule>('/amortization_schedule', 'POST', amortization_schedule);
    } catch (error) {
        console.error('Failed to save amortization_schedule:', error);
        return null;
    }
};

export const deleteAmortizationSchedule = async (id: number): Promise<AmortizationSchedule | null> => {
    try {
        return await apiRequest<AmortizationSchedule>('/amortization_schedule/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete amortization_schedule:', error);
        return null;
    }
};


export const fetchCalendar = async (): Promise<Calendar[]> => {
    try {
        return await apiRequest<Calendar[]>('/calendar/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch calendar:', error);
        return [];
    }
};

export const fetchCalendarById = async (id: number): Promise<Calendar | null> => {
    try {
        return await apiRequest<Calendar>('/calendar/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch calendar:', error);
        return null;
    }
};

export const saveCalendar = async (calendar: Calendar): Promise<Calendar | null> => {
    try {
        return await apiRequest<Calendar>('/calendar', 'POST', calendar);
    } catch (error) {
        console.error('Failed to save calendar:', error);
        return null;
    }
};

export const deleteCalendar = async (id: number): Promise<Calendar | null> => {
    try {
        return await apiRequest<Calendar>('/calendar/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete calendar:', error);
        return null;
    }
};


export const fetchHoliday = async (): Promise<Holiday[]> => {
    try {
        return await apiRequest<Holiday[]>('/holiday/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch holiday:', error);
        return [];
    }
};

export const fetchHolidayById = async (id: number): Promise<Holiday | null> => {
    try {
        return await apiRequest<Holiday>('/holiday/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch holiday:', error);
        return null;
    }
};

export const saveHoliday = async (holiday: Holiday): Promise<Holiday | null> => {
    try {
        return await apiRequest<Holiday>('/holiday', 'POST', holiday);
    } catch (error) {
        console.error('Failed to save holiday:', error);
        return null;
    }
};

export const deleteHoliday = async (id: number): Promise<Holiday | null> => {
    try {
        return await apiRequest<Holiday>('/holiday/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete holiday:', error);
        return null;
    }
};


export const fetchCurrency = async (): Promise<Currency[]> => {
    try {
        return await apiRequest<Currency[]>('/currency/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch currency:', error);
        return [];
    }
};

export const fetchCurrencyById = async (id: number): Promise<Currency | null> => {
    try {
        return await apiRequest<Currency>('/currency/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch currency:', error);
        return null;
    }
};

export const saveCurrency = async (currency: Currency): Promise<Currency | null> => {
    try {
        return await apiRequest<Currency>('/currency', 'POST', currency);
    } catch (error) {
        console.error('Failed to save currency:', error);
        return null;
    }
};

export const deleteCurrency = async (id: number): Promise<Currency | null> => {
    try {
        return await apiRequest<Currency>('/currency/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete currency:', error);
        return null;
    }
};


export const fetchCountry = async (): Promise<Country[]> => {
    try {
        return await apiRequest<Country[]>('/country/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch country:', error);
        return [];
    }
};

export const fetchCountryById = async (id: number): Promise<Country | null> => {
    try {
        return await apiRequest<Country>('/country/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch country:', error);
        return null;
    }
};

export const saveCountry = async (country: Country): Promise<Country | null> => {
    try {
        return await apiRequest<Country>('/country', 'POST', country);
    } catch (error) {
        console.error('Failed to save country:', error);
        return null;
    }
};

export const deleteCountry = async (id: number): Promise<Country | null> => {
    try {
        return await apiRequest<Country>('/country/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete country:', error);
        return null;
    }
};


export const fetchIssuer = async (): Promise<Issuer[]> => {
    try {
        return await apiRequest<Issuer[]>('/issuer/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch issuer:', error);
        return [];
    }
};

export const fetchIssuerById = async (id: number): Promise<Issuer | null> => {
    try {
        return await apiRequest<Issuer>('/issuer/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch issuer:', error);
        return null;
    }
};

export const saveIssuer = async (issuer: Issuer): Promise<Issuer | null> => {
    try {
        return await apiRequest<Issuer>('/issuer', 'POST', issuer);
    } catch (error) {
        console.error('Failed to save issuer:', error);
        return null;
    }
};

export const deleteIssuer = async (id: number): Promise<Issuer | null> => {
    try {
        return await apiRequest<Issuer>('/issuer/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete issuer:', error);
        return null;
    }
};


export const fetchSuperClass = async (): Promise<SuperClass[]> => {
    try {
        return await apiRequest<SuperClass[]>('/super_class/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch super_class:', error);
        return [];
    }
};

export const fetchSuperClassById = async (id: number): Promise<SuperClass | null> => {
    try {
        return await apiRequest<SuperClass>('/super_class/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch super_class:', error);
        return null;
    }
};

export const saveSuperClass = async (super_class: SuperClass): Promise<SuperClass | null> => {
    try {
        return await apiRequest<SuperClass>('/super_class', 'POST', super_class);
    } catch (error) {
        console.error('Failed to save super_class:', error);
        return null;
    }
};

export const deleteSuperClass = async (id: number): Promise<SuperClass | null> => {
    try {
        return await apiRequest<SuperClass>('/super_class/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete super_class:', error);
        return null;
    }
};


export const fetchAssetClass = async (): Promise<AssetClass[]> => {
    try {
        return await apiRequest<AssetClass[]>('/asset_class/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch asset_class:', error);
        return [];
    }
};

export const fetchAssetClassById = async (id: number): Promise<AssetClass | null> => {
    try {
        return await apiRequest<AssetClass>('/asset_class/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch asset_class:', error);
        return null;
    }
};

export const saveAssetClass = async (asset_class: AssetClass): Promise<AssetClass | null> => {
    try {
        return await apiRequest<AssetClass>('/asset_class', 'POST', asset_class);
    } catch (error) {
        console.error('Failed to save asset_class:', error);
        return null;
    }
};

export const deleteAssetClass = async (id: number): Promise<AssetClass | null> => {
    try {
        return await apiRequest<AssetClass>('/asset_class/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete asset_class:', error);
        return null;
    }
};


export const fetchMasterData = async (): Promise<MasterData[]> => {
    try {
        return await apiRequest<MasterData[]>('/master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch master_data:', error);
        return [];
    }
};

export const fetchMasterDataById = async (id: number): Promise<MasterData | null> => {
    try {
        return await apiRequest<MasterData>('/master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch master_data:', error);
        return null;
    }
};

export const saveMasterData = async (master_data: MasterData): Promise<MasterData | null> => {
    try {
        return await apiRequest<MasterData>('/master_data', 'POST', master_data);
    } catch (error) {
        console.error('Failed to save master_data:', error);
        return null;
    }
};

export const deleteMasterData = async (id: number): Promise<MasterData | null> => {
    try {
        return await apiRequest<MasterData>('/master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete master_data:', error);
        return null;
    }
};


export const fetchLoanMasterData = async (): Promise<LoanMasterData[]> => {
    try {
        return await apiRequest<LoanMasterData[]>('/loan_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch loan_master_data:', error);
        return [];
    }
};

export const fetchLoanMasterDataById = async (id: number): Promise<LoanMasterData | null> => {
    try {
        return await apiRequest<LoanMasterData>('/loan_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch loan_master_data:', error);
        return null;
    }
};

export const saveLoanMasterData = async (loan_master_data: LoanMasterData): Promise<LoanMasterData | null> => {
    try {
        return await apiRequest<LoanMasterData>('/loan_master_data', 'POST', loan_master_data);
    } catch (error) {
        console.error('Failed to save loan_master_data:', error);
        return null;
    }
};

export const deleteLoanMasterData = async (id: number): Promise<LoanMasterData | null> => {
    try {
        return await apiRequest<LoanMasterData>('/loan_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete loan_master_data:', error);
        return null;
    }
};


export const fetchSecurityMasterData = async (): Promise<SecurityMasterData[]> => {
    try {
        return await apiRequest<SecurityMasterData[]>('/security_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch security_master_data:', error);
        return [];
    }
};

export const findAllSmdByAssetClassXRB = async (): Promise<SecurityMasterData[]> => {
    return findAllSmdByAssetClass("XRB");
};

export const findAllSmdByAssetClassXRN = async (): Promise<SecurityMasterData[]> => {
    return findAllSmdByAssetClass("XRN");
};

export const findAllSmdByAssetClass = async (code: string): Promise<SecurityMasterData[]> => {
    try {
        return await apiRequest<SecurityMasterData[]>('/security_master_data/r06/' + code, 'GET');
    } catch (error) {
        console.error('Failed to fetch security_master_data:', error);
        return [];
    }
};

export const fetchSecurityMasterDataById = async (id: number): Promise<SecurityMasterData | null> => {
    try {
        return await apiRequest<SecurityMasterData>('/security_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch security_master_data:', error);
        return null;
    }
};

export const saveSecurityMasterData = async (security_master_data: SecurityMasterData): Promise<SecurityMasterData | null> => {
    try {
        return await apiRequest<SecurityMasterData>('/security_master_data', 'POST', security_master_data);
    } catch (error) {
        console.error('Failed to save security_master_data:', error);
        return null;
    }
};

export const deleteSecurityMasterData = async (id: number): Promise<SecurityMasterData | null> => {
    try {
        return await apiRequest<SecurityMasterData>('/security_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete security_master_data:', error);
        return null;
    }
};


export const fetchCashFlowItem = async (): Promise<CashFlowItem[]> => {
    try {
        return await apiRequest<CashFlowItem[]>('/cash_flow_item/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch cash_flow_item:', error);
        return [];
    }
};

export const fetchCashFlowItemById = async (id: number): Promise<CashFlowItem | null> => {
    try {
        return await apiRequest<CashFlowItem>('/cash_flow_item/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch cash_flow_item:', error);
        return null;
    }
};

export const saveCashFlowItem = async (cash_flow_item: CashFlowItem): Promise<CashFlowItem | null> => {
    try {
        return await apiRequest<CashFlowItem>('/cash_flow_item', 'POST', cash_flow_item);
    } catch (error) {
        console.error('Failed to save cash_flow_item:', error);
        return null;
    }
};

export const deleteCashFlowItem = async (id: number): Promise<CashFlowItem | null> => {
    try {
        return await apiRequest<CashFlowItem>('/cash_flow_item/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete cash_flow_item:', error);
        return null;
    }
};


export const fetchCashFlowReset = async (): Promise<CashFlowReset[]> => {
    try {
        return await apiRequest<CashFlowReset[]>('/cash_flow_reset/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch cash_flow_reset:', error);
        return [];
    }
};

export const fetchCashFlowResetById = async (id: number): Promise<CashFlowReset | null> => {
    try {
        return await apiRequest<CashFlowReset>('/cash_flow_reset/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch cash_flow_reset:', error);
        return null;
    }
};

export const saveCashFlowReset = async (cash_flow_reset: CashFlowReset): Promise<CashFlowReset | null> => {
    try {
        return await apiRequest<CashFlowReset>('/cash_flow_reset', 'POST', cash_flow_reset);
    } catch (error) {
        console.error('Failed to save cash_flow_reset:', error);
        return null;
    }
};

export const deleteCashFlowReset = async (id: number): Promise<CashFlowReset | null> => {
    try {
        return await apiRequest<CashFlowReset>('/cash_flow_reset/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete cash_flow_reset:', error);
        return null;
    }
};


export const fetchSettlementType = async (): Promise<SettlementType[]> => {
    try {
        return await apiRequest<SettlementType[]>('/settlement_type/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch settlement_type:', error);
        return [];
    }
};

export const fetchSettlementTypeById = async (id: number): Promise<SettlementType | null> => {
    try {
        return await apiRequest<SettlementType>('/settlement_type/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch settlement_type:', error);
        return null;
    }
};

export const saveSettlementType = async (settlement_type: SettlementType): Promise<SettlementType | null> => {
    try {
        return await apiRequest<SettlementType>('/settlement_type', 'POST', settlement_type);
    } catch (error) {
        console.error('Failed to save settlement_type:', error);
        return null;
    }
};

export const deleteSettlementType = async (id: number): Promise<SettlementType | null> => {
    try {
        return await apiRequest<SettlementType>('/settlement_type/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete settlement_type:', error);
        return null;
    }
};


export const fetchFutureMasterData = async (): Promise<FutureMasterData[]> => {
    try {
        return await apiRequest<FutureMasterData[]>('/future_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch future_master_data:', error);
        return [];
    }
};

export const fetchFutureMasterDataById = async (id: number): Promise<FutureMasterData | null> => {
    try {
        return await apiRequest<FutureMasterData>('/future_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch future_master_data:', error);
        return null;
    }
};

export const saveFutureMasterData = async (future_master_data: FutureMasterData): Promise<FutureMasterData | null> => {
    try {
        return await apiRequest<FutureMasterData>('/future_master_data', 'POST', future_master_data);
    } catch (error) {
        console.error('Failed to save future_master_data:', error);
        return null;
    }
};

export const deleteFutureMasterData = async (id: number): Promise<FutureMasterData | null> => {
    try {
        return await apiRequest<FutureMasterData>('/future_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete future_master_data:', error);
        return null;
    }
};

export const fetchBondFutureMasterData = async (): Promise<BondFutureMasterData[]> => {
    try {
        return await apiRequest<BondFutureMasterData[]>('/bond_future_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch bond_future_master_data:', error);
        return [];
    }
};

export const fetchBondFutureMasterDataByAssetClass = async (): Promise<BondFutureMasterData[]> => {
    try {
        return await apiRequest<BondFutureMasterData[]>('/bond_future_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch bond_future_master_data:', error);
        return [];
    }
};

export const fetchBondFutureMasterDataById = async (id: number): Promise<BondFutureMasterData | null> => {
    try {
        return await apiRequest<BondFutureMasterData>('/bond_future_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch bond_future_master_data:', error);
        return null;
    }
};

export const saveBondFutureMasterData = async (bond_future_master_data: BondFutureMasterData): Promise<BondFutureMasterData | null> => {
    try {
        return await apiRequest<BondFutureMasterData>('/bond_future_master_data', 'POST', bond_future_master_data);
    } catch (error) {
        console.error('Failed to save bond_future_master_data:', error);
        return null;
    }
};

export const deleteBondFutureMasterData = async (id: number): Promise<BondFutureMasterData | null> => {
    try {
        return await apiRequest<BondFutureMasterData>('/bond_future_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete bond_future_master_data:', error);
        return null;
    }
};


export const fetchDeliverableBonds = async (): Promise<DeliverableBonds[]> => {
    try {
        return await apiRequest<DeliverableBonds[]>('/deliverable_bonds/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch deliverable_bonds:', error);
        return [];
    }
};

export const fetchDeliverableBondsById = async (id: number): Promise<DeliverableBonds | null> => {
    try {
        return await apiRequest<DeliverableBonds>('/deliverable_bonds/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch deliverable_bonds:', error);
        return null;
    }
};

export const saveDeliverableBonds = async (deliverable_bonds: DeliverableBonds): Promise<DeliverableBonds | null> => {
    try {
        return await apiRequest<DeliverableBonds>('/deliverable_bonds', 'POST', deliverable_bonds);
    } catch (error) {
        console.error('Failed to save deliverable_bonds:', error);
        return null;
    }
};

export const deleteDeliverableBonds = async (id: number): Promise<DeliverableBonds | null> => {
    try {
        return await apiRequest<DeliverableBonds>('/deliverable_bonds/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete deliverable_bonds:', error);
        return null;
    }
};


export const fetchFxFutureMasterData = async (): Promise<FxFutureMasterData[]> => {
    try {
        return await apiRequest<FxFutureMasterData[]>('/fx_future_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch fx_future_master_data:', error);
        return [];
    }
};

export const fetchFxFutureMasterDataById = async (id: number): Promise<FxFutureMasterData | null> => {
    try {
        return await apiRequest<FxFutureMasterData>('/fx_future_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch fx_future_master_data:', error);
        return null;
    }
};

export const saveFxFutureMasterData = async (fx_future_master_data: FxFutureMasterData): Promise<FxFutureMasterData | null> => {
    try {
        return await apiRequest<FxFutureMasterData>('/fx_future_master_data', 'POST', fx_future_master_data);
    } catch (error) {
        console.error('Failed to save fx_future_master_data:', error);
        return null;
    }
};

export const deleteFxFutureMasterData = async (id: number): Promise<FxFutureMasterData | null> => {
    try {
        return await apiRequest<FxFutureMasterData>('/fx_future_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete fx_future_master_data:', error);
        return null;
    }
};


export const fetchMmFutureMasterData = async (): Promise<MmFutureMasterData[]> => {
    try {
        return await apiRequest<MmFutureMasterData[]>('/mm_future_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch mm_future_master_data:', error);
        return [];
    }
};

export const fetchMmFutureMasterDataById = async (id: number): Promise<MmFutureMasterData | null> => {
    try {
        return await apiRequest<MmFutureMasterData>('/mm_future_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch mm_future_master_data:', error);
        return null;
    }
};

export const saveMmFutureMasterData = async (mm_future_master_data: MmFutureMasterData): Promise<MmFutureMasterData | null> => {
    try {
        return await apiRequest<MmFutureMasterData>('/mm_future_master_data', 'POST', mm_future_master_data);
    } catch (error) {
        console.error('Failed to save mm_future_master_data:', error);
        return null;
    }
};

export const deleteMmFutureMasterData = async (id: number): Promise<MmFutureMasterData | null> => {
    try {
        return await apiRequest<MmFutureMasterData>('/mm_future_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete mm_future_master_data:', error);
        return null;
    }
};


export const fetchInstrumentQuote = async (): Promise<InstrumentQuote[]> => {
    try {
        return await apiRequest<InstrumentQuote[]>('/instrument_quote/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch instrument_quote:', error);
        return [];
    }
};

export const fetchInstrumentQuoteById = async (id: number): Promise<InstrumentQuote | null> => {
    try {
        return await apiRequest<InstrumentQuote>('/instrument_quote/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch instrument_quote:', error);
        return null;
    }
};

export const saveInstrumentQuote = async (instrument_quote: InstrumentQuote): Promise<InstrumentQuote | null> => {
    try {
        return await apiRequest<InstrumentQuote>('/instrument_quote', 'POST', instrument_quote);
    } catch (error) {
        console.error('Failed to save instrument_quote:', error);
        return null;
    }
};

export const deleteInstrumentQuote = async (id: number): Promise<InstrumentQuote | null> => {
    try {
        return await apiRequest<InstrumentQuote>('/instrument_quote/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete instrument_quote:', error);
        return null;
    }
};


export const fetchInstrumentQuoteHist = async (): Promise<InstrumentQuoteHist[]> => {
    try {
        return await apiRequest<InstrumentQuoteHist[]>('/instrument_quote_hist/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch instrument_quote_hist:', error);
        return [];
    }
};

export const fetchInstrumentQuoteHistById = async (id: number): Promise<InstrumentQuoteHist | null> => {
    try {
        return await apiRequest<InstrumentQuoteHist>('/instrument_quote_hist/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch instrument_quote_hist:', error);
        return null;
    }
};

export const saveInstrumentQuoteHist = async (instrument_quote_hist: InstrumentQuoteHist): Promise<InstrumentQuoteHist | null> => {
    try {
        return await apiRequest<InstrumentQuoteHist>('/instrument_quote_hist', 'POST', instrument_quote_hist);
    } catch (error) {
        console.error('Failed to save instrument_quote_hist:', error);
        return null;
    }
};

export const deleteInstrumentQuoteHist = async (id: number): Promise<InstrumentQuoteHist | null> => {
    try {
        return await apiRequest<InstrumentQuoteHist>('/instrument_quote_hist/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete instrument_quote_hist:', error);
        return null;
    }
};


export const fetchForexMasterData = async (): Promise<ForexMasterData[]> => {
    try {
        return await apiRequest<ForexMasterData[]>('/forex_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch forex_master_data:', error);
        return [];
    }
};

export const fetchForexMasterDataById = async (id: number): Promise<ForexMasterData | null> => {
    try {
        return await apiRequest<ForexMasterData>('/forex_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch forex_master_data:', error);
        return null;
    }
};

export const saveForexMasterData = async (forex_master_data: ForexMasterData): Promise<ForexMasterData | null> => {
    try {
        return await apiRequest<ForexMasterData>('/forex_master_data', 'POST', forex_master_data);
    } catch (error) {
        console.error('Failed to save forex_master_data:', error);
        return null;
    }
};

export const deleteForexMasterData = async (id: number): Promise<ForexMasterData | null> => {
    try {
        return await apiRequest<ForexMasterData>('/forex_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete forex_master_data:', error);
        return null;
    }
};


export const fetchCounterpartyType = async (): Promise<CounterpartyType[]> => {
    try {
        return await apiRequest<CounterpartyType[]>('/counterparty_type/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch counterparty_type:', error);
        return [];
    }
};

export const fetchCounterpartyTypeById = async (id: number): Promise<CounterpartyType | null> => {
    try {
        return await apiRequest<CounterpartyType>('/counterparty_type/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch counterparty_type:', error);
        return null;
    }
};

export const saveCounterpartyType = async (counterparty_type: CounterpartyType): Promise<CounterpartyType | null> => {
    try {
        return await apiRequest<CounterpartyType>('/counterparty_type', 'POST', counterparty_type);
    } catch (error) {
        console.error('Failed to save counterparty_type:', error);
        return null;
    }
};

export const deleteCounterpartyType = async (id: number): Promise<CounterpartyType | null> => {
    try {
        return await apiRequest<CounterpartyType>('/counterparty_type/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete counterparty_type:', error);
        return null;
    }
};


export const fetchCounterparty = async (): Promise<Counterparty[]> => {
    try {
        return await apiRequest<Counterparty[]>('/counterparty/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch counterparty:', error);
        return [];
    }
};

export const fetchCounterpartyById = async (id: number): Promise<Counterparty | null> => {
    try {
        return await apiRequest<Counterparty>('/counterparty/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch counterparty:', error);
        return null;
    }
};

export const saveCounterparty = async (counterparty: Counterparty): Promise<Counterparty | null> => {
    try {
        return await apiRequest<Counterparty>('/counterparty', 'POST', counterparty);
    } catch (error) {
        console.error('Failed to save counterparty:', error);
        return null;
    }
};

export const deleteCounterparty = async (id: number): Promise<Counterparty | null> => {
    try {
        return await apiRequest<Counterparty>('/counterparty/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete counterparty:', error);
        return null;
    }
};


export const fetchPortfolioMasterData = async (): Promise<PortfolioMasterData[]> => {
    try {
        return await apiRequest<PortfolioMasterData[]>('/portfolio_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch portfolio_master_data:', error);
        return [];
    }
};

export const fetchPortfolioMasterDataById = async (id: number): Promise<PortfolioMasterData | null> => {
    try {
        return await apiRequest<PortfolioMasterData>('/portfolio_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch portfolio_master_data:', error);
        return null;
    }
};

export const savePortfolioMasterData = async (portfolio_master_data: PortfolioMasterData): Promise<PortfolioMasterData | null> => {
    try {
        return await apiRequest<PortfolioMasterData>('/portfolio_master_data', 'POST', portfolio_master_data);
    } catch (error) {
        console.error('Failed to save portfolio_master_data:', error);
        return null;
    }
};

export const deletePortfolioMasterData = async (id: number): Promise<PortfolioMasterData | null> => {
    try {
        return await apiRequest<PortfolioMasterData>('/portfolio_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete portfolio_master_data:', error);
        return null;
    }
};


export const fetchPositionMasterData = async (): Promise<PositionMasterData[]> => {
    try {
        return await apiRequest<PositionMasterData[]>('/position_master_data/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch position_master_data:', error);
        return [];
    }
};

export const fetchPositionMasterDataById = async (id: number): Promise<PositionMasterData | null> => {
    try {
        return await apiRequest<PositionMasterData>('/position_master_data/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch position_master_data:', error);
        return null;
    }
};

export const savePositionMasterData = async (position_master_data: PositionMasterData): Promise<PositionMasterData | null> => {
    try {
        return await apiRequest<PositionMasterData>('/position_master_data', 'POST', position_master_data);
    } catch (error) {
        console.error('Failed to save position_master_data:', error);
        return null;
    }
};

export const deletePositionMasterData = async (id: number): Promise<PositionMasterData | null> => {
    try {
        return await apiRequest<PositionMasterData>('/position_master_data/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete position_master_data:', error);
        return null;
    }
};


export const fetchPositionDetail = async (): Promise<PositionDetail[]> => {
    try {
        return await apiRequest<PositionDetail[]>('/position_detail/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch position_detail:', error);
        return [];
    }
};

export const fetchPositionDetailById = async (id: number): Promise<PositionDetail | null> => {
    try {
        return await apiRequest<PositionDetail>('/position_detail/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch position_detail:', error);
        return null;
    }
};

export const savePositionDetail = async (position_detail: PositionDetail): Promise<PositionDetail | null> => {
    try {
        return await apiRequest<PositionDetail>('/position_detail', 'POST', position_detail);
    } catch (error) {
        console.error('Failed to save position_detail:', error);
        return null;
    }
};

export const deletePositionDetail = async (id: number): Promise<PositionDetail | null> => {
    try {
        return await apiRequest<PositionDetail>('/position_detail/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete position_detail:', error);
        return null;
    }
};


export const fetchTxnStatus = async (): Promise<TxnStatus[]> => {
    try {
        return await apiRequest<TxnStatus[]>('/txn_status/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch txn_status:', error);
        return [];
    }
};

export const fetchTxnStatusById = async (id: number): Promise<TxnStatus | null> => {
    try {
        return await apiRequest<TxnStatus>('/txn_status/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch txn_status:', error);
        return null;
    }
};

export const saveTxnStatus = async (txn_status: TxnStatus): Promise<TxnStatus | null> => {
    try {
        return await apiRequest<TxnStatus>('/txn_status', 'POST', txn_status);
    } catch (error) {
        console.error('Failed to save txn_status:', error);
        return null;
    }
};

export const deleteTxnStatus = async (id: number): Promise<TxnStatus | null> => {
    try {
        return await apiRequest<TxnStatus>('/txn_status/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete txn_status:', error);
        return null;
    }
};

export const fetchFinancialTxn = async (): Promise<FinancialTxn[]> => {
    try {
        return await apiRequest<FinancialTxn[]>('/financial_txn/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch financial_txn:', error);
        return [];
    }
};

export const findAllByAssetClass = async (code: string): Promise<FinancialTxnDto[]> => {
    try {
        return await apiRequest<FinancialTxnDto[]>('/financial_txn/r03/' + code, 'GET');
    } catch (error) {
        console.error('Failed to fetch financial_txn:', error);
        return [];
    }
};

export const fetchFinancialTxnById = async (id: number): Promise<FinancialTxnDto | null> => {
    try {
        return await apiRequest<FinancialTxnDto>('/financial_txn/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch financial_txn:', error);
        return null;
    }
};

export const saveFinancialTxn = async (financial_txn: FinancialTxnDto): Promise<FinancialTxnDto | null> => {
    try {
        return await apiRequest<FinancialTxnDto>('/financial_txn', 'POST', financial_txn);
    } catch (error) {
        console.error('Failed to save financial_txn:', error);
        return null;
    }
};

export const deleteFinancialTxn = async (id: number): Promise<FinancialTxn | null> => {
    try {
        return await apiRequest<FinancialTxn>('/financial_txn/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete financial_txn:', error);
        return null;
    }
};

export const logicalDeleteFinancialTxn = async (id: number): Promise<FinancialTxn | null> => {
    try {
        return await apiRequest<FinancialTxn>('/financial_txn/d02/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete financial_txn:', error);
        return null;
    }
};

export const fetchYieldCurve = async (): Promise<YieldCurve[]> => {
    try {
        return await apiRequest<YieldCurve[]>('/yield_curve/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch yield_curve:', error);
        return [];
    }
};

export const fetchYieldCurveById = async (id: number): Promise<YieldCurve | null> => {
    try {
        return await apiRequest<YieldCurve>('/yield_curve/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch yield_curve:', error);
        return null;
    }
};

export const saveYieldCurve = async (yield_curve: YieldCurve): Promise<YieldCurve | null> => {
    try {
        return await apiRequest<YieldCurve>('/yield_curve', 'POST', yield_curve);
    } catch (error) {
        console.error('Failed to save yield_curve:', error);
        return null;
    }
};

export const deleteYieldCurve = async (id: number): Promise<YieldCurve | null> => {
    try {
        return await apiRequest<YieldCurve>('/yield_curve/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete yield_curve:', error);
        return null;
    }
};

export const fetchYieldCurveItem = async (): Promise<YieldCurveItem[]> => {
    try {
        return await apiRequest<YieldCurveItem[]>('/yield_curve_item/r01', 'GET');
    } catch (error) {
        console.error('Failed to fetch yield_curve_item:', error);
        return [];
    }
};

export const fetchYieldCurveItemById = async (id: number): Promise<YieldCurveItem | null> => {
    try {
        return await apiRequest<YieldCurveItem>('/yield_curve_item/r02/' + id, 'GET');
    } catch (error) {
        console.error('Failed to fetch yield_curve_item:', error);
        return null;
    }
};

export const saveYieldCurveItem = async (yield_curve_item: YieldCurveItem): Promise<YieldCurveItem | null> => {
    try {
        return await apiRequest<YieldCurveItem>('/yield_curve_item', 'POST', yield_curve_item);
    } catch (error) {
        console.error('Failed to save yield_curve_item:', error);
        return null;
    }
};

export const deleteYieldCurveItem = async (id: number): Promise<YieldCurveItem | null> => {
    try {
        return await apiRequest<YieldCurveItem>('/yield_curve_item/d01/' + id, 'DELETE');
    } catch (error) {
        console.error('Failed to delete yield_curve_item:', error);
        return null;
    }
};

export async function fetchPositionProspect(filter: ProspectFilter): Promise<PositionProspectDto[]> {

    try {

        const cleanPayload = {
            positionId: filter?.positionId !== undefined
                ? filter.positionId
                : (typeof filter?.positionId === 'number' ? filter.positionId : null),

            counterpartyId: filter?.counterpartyId !== undefined
                ? filter.counterpartyId
                : (typeof filter?.counterpartyId === 'number' ? filter.counterpartyId : null),

            assetClassId: filter?.assetClassId !== undefined
                ? filter.assetClassId
                : (typeof filter?.assetClassId === 'number' ? filter.assetClassId : null)

        };

        console.log("Payload pulito inviato al server:", cleanPayload);
        return await apiRequest<PositionProspectDto[]>('/prospects/position', 'POST', cleanPayload);
    } catch (error) {
        console.error('Failed to fetch position prospect data:', error);
        return [];
    }
}


