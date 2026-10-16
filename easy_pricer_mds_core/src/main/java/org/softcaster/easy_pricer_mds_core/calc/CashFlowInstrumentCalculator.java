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
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.easy_pricer_mds_core.helpers.FltSecurityMasterDataHelper;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.CashFlowInputData;
import org.softcaster.engine.dto.CashFlowOutputData;
import org.softcaster.engine.dto.FRBInputData;
import org.softcaster.engine.dto.FRBOutputData;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("cashFlowInstrumentCalculator")
public class CashFlowInstrumentCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    @Qualifier("bondPricer")
    private BondPricer bondPricer;
    @Autowired
    @Qualifier("fltSecurityMasterDataHelper")
    private FltSecurityMasterDataHelper fltHelper;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    public XRBOutputData fixedIncomeValuation(XRBInputData input, SecurityMasterData securityMasterData) {
        XRBOutputData output = null;
        if (securityMasterData != null) {
            if (!securityMasterData.getCashFlows().isEmpty()) {
                List<CashFlow> flows = Utils.convertCashFlow(securityMasterData.getCashFlows());
                input.setFlows(flows);
            }

            output = bondPricer.calculate(input);
        }

        return output;
    }

    public FRBOutputData floatingRateValuation(FRBInputData input, MasterData masterData) {
        if (masterData instanceof FltSecurityMasterData fsmd) {
            FRBOutputData output = null;
            if (fsmd != null && fsmd.getCashFlows() != null && !fsmd.getCashFlows().isEmpty()) {
                List<CashFlow> flows = fltHelper.completeFltSecurityCashFlow(fsmd);
                input.setFlows(flows);
                output = bondPricer.calculate(input);
            }
            return output;
        } else {
            throw new IllegalArgumentException("Invalid Master Data");
        }
    }

    public CashFlowOutputData cashFlowValuation(CashFlowInputData input, MasterData masterData) {
        if (masterData == null) {
            if (input.getCode() == null || input.getCode().isEmpty()) {
                throw new IllegalArgumentException("Invalid Master Data");
            }

            masterData = smdDAO.findByCodeWithCashFlowAndHolidays(input.getCode());
            if (masterData == null) {
                throw new IllegalArgumentException("Invalid Master Data");
            }
        }
        input.setCompounding(masterData.getCompounding());
        input.setDaycount(masterData.getAccrualDaycount());
        input.setFrequency(masterData.getFrequency());
        input.setRedemptionPrice(masterData.getRedempionPrice());
        input.setCode(masterData.getCode());
        Calendar calendar = new Calendar(masterData.getCurrency());
        input.setValuationDate(calendar.getNextBusinessDate(marketDataService.getOfficialDate(), masterData.getBusinessDays()));

        CashFlowOutputData output = null;
        if (masterData instanceof FltSecurityMasterData fsmd) {
            input.setProjectionMethod(fsmd.getCouponPm());
            FRBOutputData frbo = floatingRateValuation(input, fsmd);
            output = getCashFlowOutputData(frbo);
        } else if (masterData instanceof SecurityMasterData smd) {
            XRBOutputData xrbo = fixedIncomeValuation(input, smd);
            output = getCashFlowOutputData(xrbo);
        }

        return output;
    }

    public BondPricingResponse bondValuation(BondPricingRequest request) {
        BondPricingResponse response = null;
        if (smdDAO != null) {
            SecurityMasterData securityMasterData = smdDAO.findByCodeWithCashFlowAndHolidays(request.isin);
            if (securityMasterData != null) {
                FRBInputData input = new FRBInputData();
                Calendar calendar = new Calendar(securityMasterData.getCurrency());
                LocalDate valuationDate = calendar.getNextBusinessDate(request.referenceDate, securityMasterData.getBusinessDays());
                input.setValuationDate(valuationDate);
                input.setReferencePrice(request.referencePrice);
                input.setReferenceRate(request.referenceRate);
                input.setRedemptionPrice(securityMasterData.getRedempionPrice());
                input.setFrequency(securityMasterData.getFrequency());
                input.setDaycount(securityMasterData.getAccrualDaycount());
                input.setCompounding(securityMasterData.getCompounding());

                XRBOutputData output = fixedIncomeValuation(input, securityMasterData);
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

    public double getAccruals(SecurityMasterData securityMasterData, LocalDate accrualDate) {
        double accruals = 0.;
        List<CashFlow> flows = Utils.convertCashFlow(securityMasterData.getCashFlows());
        accruals = bondPricer.calculateAccruedInterest(flows, accrualDate, securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());
        return accruals;
    }

    public double getZSpread(SecurityMasterData securityMasterData,
            double dirtyPrice,
            LocalDate valuationDate,
            DiscountCurve curve) {

        double zSpread = 0.;
        List<CashFlow> cashflows = Utils.convertCashFlow(securityMasterData.getCashFlows());
        zSpread = bondPricer.calculateZSpread(cashflows, dirtyPrice, valuationDate, securityMasterData.getAccrualDaycount(),
                securityMasterData.getCompounding(), securityMasterData.getFrequency(), curve);

        return zSpread;
    }

    public double repriceBondForYieldShift(SecurityMasterData securityMasterData, LocalDate officialDate, double ytm, double basisPoints) {

        double yieldShift = basisPoints / 100.;
        ytm += yieldShift;

        Calendar calendar = new Calendar(securityMasterData.getCurrency());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = bondPricer.calculatePrice(Utils.convertCashFlow(securityMasterData.getCashFlows()), ytm, valuationDate,
                securityMasterData.getAccrualDaycount(), securityMasterData.getCompounding(), securityMasterData.getFrequency());

        return newPrice;
    }

    public double calculatePrice(SecurityMasterData securityMasterData, LocalDate officialDate, DiscountCurve discountCurve) {

        Currency currency = currencyDAO.findByIsoCode(securityMasterData.getCurrency().getIsoCode());
        Calendar calendar = new Calendar(currency);
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = bondPricer.calculatePrice(Utils.convertCashFlow(securityMasterData.getCashFlows()), discountCurve, valuationDate,
                securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());

        return newPrice;
    }

    private CashFlowOutputData getCashFlowOutputData(FRBOutputData frbo) {
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
    
    private CashFlowOutputData getCashFlowOutputData(XRBOutputData xrbo) {
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
}
