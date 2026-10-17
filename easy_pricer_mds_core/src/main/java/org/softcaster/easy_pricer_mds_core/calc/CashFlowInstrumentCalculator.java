package org.softcaster.easy_pricer_mds_core.calc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FltSecurityMasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.easy_pricer_mds_core.helpers.FltSecurityMasterDataHelper;
import org.softcaster.engine.analytics.FRBPricer;
import org.softcaster.engine.analytics.XRBPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.FRBInput;
import org.softcaster.engine.dto.FRBResult;
import org.softcaster.engine.dto.FloatingCouponSpec;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("cashFlowInstrumentCalculator")
public class CashFlowInstrumentCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    private CurrencyDAO currencyDAO;
    @Autowired
    @Qualifier("xRBPricer")
    private XRBPricer xRBPricer;
    @Autowired
    @Qualifier("fRBPricer")
    private FRBPricer fRBPricer;
    @Autowired
    @Qualifier("fltSecurityMasterDataHelper")
    private FltSecurityMasterDataHelper fltHelper;

    // ------------------------------------------------------------------------
    // -- Fixed Rate Bond
    // ------------------------------------------------------------------------
    public XRBInput getXRBInput(LocalDate officialDate, double cleanPrice, SecurityMasterData securityMasterData) {
        if (securityMasterData == null || securityMasterData.getCashFlows().isEmpty()) {
            return null;
        }
        // creazioni dati schedule
        List<CashFlow> flows = Utils.convertCashFlow(securityMasterData.getCashFlows());
        CashFlowSchedule schedule = new CashFlowSchedule(flows, securityMasterData.getAccrualDaycount(),
                securityMasterData.getFrequency(), securityMasterData.getCompounding());
        // Calcolo valuationDate
        Calendar calendar = new Calendar(securityMasterData.getCurrencyList());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());
        XRBInput input = new XRBInput(securityMasterData.getCode(), valuationDate, cleanPrice, schedule);
        return input;
    }

    public XRBInput getXRBInput(String code, LocalDate officialDate, double cleanPrice) {
        if (code == null || code.isBlank()) {
            return null;
        }

        SecurityMasterData securityMasterData = smdDAO.findByCodeWithCashFlowAndHolidays(code);
        return getXRBInput(officialDate, cleanPrice, securityMasterData);
    }

    public XRBResult fixedIncomeValuation(XRBInput input) {
        if (input == null) {
            return null;
        }
        return xRBPricer.calculate(input);
    }

    // ------------------------------------------------------------------------
    // -- Floating Rate Bond
    // ------------------------------------------------------------------------
    public FRBInput getFRBInput(LocalDate officialDate, double cleanPrice, FloatingCouponSpec spec, FltSecurityMasterData fltSecurityMasterData) {
        if (fltSecurityMasterData == null || fltSecurityMasterData.getCashFlows().isEmpty()) {
            return null;
        }
        // creazioni dati schedule
        List<CashFlow> flows = fltHelper.completeFltSecurityCashFlow(fltSecurityMasterData);
        CashFlowSchedule schedule = new CashFlowSchedule(flows, fltSecurityMasterData.getAccrualDaycount(),
                fltSecurityMasterData.getFrequency(), fltSecurityMasterData.getCompounding());
        // Calcolo valuationDate
        Calendar calendar = new Calendar(fltSecurityMasterData.getCurrencyList());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, fltSecurityMasterData.getBusinessDays());
        FRBInput input = new FRBInput(fltSecurityMasterData.getCode(), valuationDate, cleanPrice, schedule, spec);
        return input;
    }

    public FRBInput getFRBInput(String code, LocalDate officialDate, double cleanPrice, FloatingCouponSpec spec) {
        if (code == null || code.isBlank()) {
            return null;
        }

        SecurityMasterData securityMasterData = smdDAO.findByCodeWithCashFlowAndHolidays(code);
        if (securityMasterData instanceof FltSecurityMasterData fltSecurityMasterData) {
            return getFRBInput(officialDate, cleanPrice, spec, fltSecurityMasterData);
        }

        return null;
    }

    public FRBResult fltIncomeValuation(FRBInput input) {
        if (input == null) {
            return null;
        }
        return fRBPricer.calculate(input);
    }

    public double getAccruals(SecurityMasterData securityMasterData, LocalDate accrualDate) {
        double accruals = 0.;
        List<CashFlow> flows = Utils.convertCashFlow(securityMasterData.getCashFlows());
        accruals = xRBPricer.calculateAccruedInterest(flows, accrualDate, securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());
        return accruals;
    }

    public double getZSpread(SecurityMasterData securityMasterData,
            double dirtyPrice,
            LocalDate valuationDate,
            DiscountCurve curve) {

        double zSpread = 0.;
        List<CashFlow> cashflows = Utils.convertCashFlow(securityMasterData.getCashFlows());
        zSpread = xRBPricer.calculateZSpread(cashflows, dirtyPrice, valuationDate, securityMasterData.getAccrualDaycount(),
                securityMasterData.getCompounding(), securityMasterData.getFrequency(), curve);

        return zSpread;
    }

    public double repriceBondForYieldShift(SecurityMasterData securityMasterData, LocalDate officialDate, double ytm, double basisPoints) {

        double yieldShift = basisPoints / 100.;
        ytm += yieldShift;

        Calendar calendar = new Calendar(securityMasterData.getCurrency());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = xRBPricer.calculatePrice(Utils.convertCashFlow(securityMasterData.getCashFlows()), ytm, valuationDate,
                securityMasterData.getAccrualDaycount(), securityMasterData.getCompounding(), securityMasterData.getFrequency());

        return newPrice;
    }

    public double calculatePrice(SecurityMasterData securityMasterData, LocalDate officialDate, DiscountCurve discountCurve) {

        Currency currency = currencyDAO.findByIsoCode(securityMasterData.getCurrency().getIsoCode());
        Calendar calendar = new Calendar(currency);
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = xRBPricer.calculatePrice(Utils.convertCashFlow(securityMasterData.getCashFlows()), discountCurve, valuationDate,
                securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());

        return newPrice;
    }

    /*
    private CashFlowOutputData getCashFlowOutputData(FRBOutputData2 frbo) {
        CashFlowOutputData output = new CashFlowOutputData();
        output.setAccruedInterest(frbo.getAccruedInterest());
        output.setAcpList(frbo.getAcpList());
        output.setDiscountMargin(frbo.getDiscountMargin());
        output.setDuration(frbo.getDuration());
        output.setDv01(frbo.getDv01());
        output.setMarginDuration(frbo.getMacaulayDuration());
        output.setMarginDuration(frbo.getMarginDuration());
        output.setModifiedDuration(frbo.getModifiedDuration());
        output.setPrice(frbo.getPrice());
        output.setShortBondYield(frbo.getShortBondYield());
        output.setValuationDate(frbo.getValuationDate());
        output.setYtm(frbo.getYtm());
        return output;
    }
    
    private CashFlowOutputData getCashFlowOutputData(XRBOutputData2 xrbo) {
        CashFlowOutputData output = new CashFlowOutputData();
        output.setAccruedInterest(xrbo.getAccruedInterest());
        output.setAcpList(xrbo.getAcpList());
        output.setDuration(xrbo.getDuration());
        output.setDv01(xrbo.getDv01());
        output.setMarginDuration(xrbo.getMacaulayDuration());
        output.setModifiedDuration(xrbo.getModifiedDuration());
        output.setPrice(xrbo.getPrice());
        output.setValuationDate(xrbo.getValuationDate());
        output.setYtm(xrbo.getYtm());
        return output;
    }
    
    public BondPricingResponse bondValuation(BondPricingRequest request) {
        BondPricingResponse response = null;
        if (smdDAO != null) {
            SecurityMasterData securityMasterData = smdDAO.findByCodeWithCashFlowAndHolidays(request.isin);
            if (securityMasterData != null) {
                FRBInput input = new FRBInput();
                Calendar calendar = new Calendar(securityMasterData.getCurrency());
                LocalDate valuationDate = calendar.getNextBusinessDate(request.referenceDate, securityMasterData.getBusinessDays());
                input.setValuationDate(valuationDate);
                input.setReferencePrice(request.referencePrice);
                input.setReferenceRate(request.referenceRate);
                input.setRedemptionPrice(securityMasterData.getRedempionPrice());
                input.setFrequency(securityMasterData.getFrequency());
                input.setDaycount(securityMasterData.getAccrualDaycount());
                input.setCompounding(securityMasterData.getCompounding());

                XRBOutputData2 output = fixedIncomeValuation(input, securityMasterData);
                if (output != null) {
                    response = new BondPricingResponse();
                    response.accruedInterest = output.getAccruedInterest();
                    response.macaulayDuration = 0.;
                    response.yieldToMaturity = output.getYtm();
                    response.modifiedDuration = output.getModifiedDuration();
                    response.dv01 = output.getDv01();
                    response.convexity = 0.;
                    response.presentValue = 0.;
                    response.yieldToMaturityPV = 0.;
                    response.acpList = output.getAcpList();
                }
            }
        }

        return response;
    }
    
     */
    public BondPricingResponse bondValuation(BondPricingRequest request) {
        XRBInput input = getXRBInput(request.isin, request.referenceDate.toLocalDate(), request.referencePrice);
        XRBResult result = fixedIncomeValuation(input);
        if (result == null) {
            return null;
        }
        BondPricingResponse response = new BondPricingResponse();
        response.accruedInterest = result.analytics().accruedInterest();
        response.convexity = result.analytics().convexity();
        response.discountMargin = 0.;
        response.dv01 = result.analytics().dv01();
        response.macaulayDuration = result.analytics().modifiedDuration();
        response.marginDuration = 0.;
        response.modifiedDuration = result.analytics().modifiedDuration();
        response.presentValue = 0.;
        response.shortBondYield = 0.;
        response.yieldToMaturity = result.analytics().yield();
        response.yieldToMaturityPV = 0.;
        
        return response;
    }
}
