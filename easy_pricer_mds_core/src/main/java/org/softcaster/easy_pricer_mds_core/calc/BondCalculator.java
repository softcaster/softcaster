package org.softcaster.easy_pricer_mds_core.calc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.Frequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("bondCalculator")
public class BondCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    @Qualifier("bondPricer") 
    private BondPricer bondPricer;

    private List<CashFlow> getCashFlow(List<CashFlowItem> cfList) {
        List<CashFlow> flows = new ArrayList<>();
        for (CashFlowItem item : cfList) {
            CashFlow flow = new CashFlow(
                    item.getStartDate().toLocalDate(),
                    item.getEnddate().toLocalDate(),
                    item.getEnddate().toLocalDate(),
                    item.getAmount(),
                    item.getInterest(),
                    0.,
                    CashFlowStatus.RECORDED
            );
            flows.add(flow);
        }
        return flows;
    }

    public XRBOutputData bondValuation(XRBInputData input, SecurityMasterData securityMasterData) {
        XRBOutputData output = null;
        if (securityMasterData != null) {
            if (!securityMasterData.getCashFlows().isEmpty()) {
                List<CashFlow> flows = getCashFlow(securityMasterData.getCashFlows());
                input.setFlows(flows);
            }

            output = bondPricer.calculate(input);
        }

        return output;
    }

    public BondPricingResponse bondValuation(BondPricingRequest request) {
        BondPricingResponse response = null;
        if (smdDAO != null) {
            SecurityMasterData securityMasterData = smdDAO.findByIsin(request.isin);
            if (securityMasterData != null) {
                XRBInputData input = new XRBInputData();
                Calendar calendar = new Calendar(securityMasterData.getCurrency());
                LocalDate valuationDate = calendar.getNextBusinessDate(request.referenceDate, securityMasterData.getBusinessDays());
                input.setValuationDate(valuationDate);
                input.setReferencePrice(request.referencePrice);
                input.setFrequency(Frequency.fromCode(securityMasterData.getFrequency().getCode()));
                input.setDaycount(securityMasterData.getAccrualDaycount());
                input.setCompounding(Compounding.COMPOUNDED);

                XRBOutputData output = bondValuation(input, securityMasterData);
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
                }
            }
        }

        return response;
    }

    public double getAccruals(SecurityMasterData securityMasterData, LocalDate accrualDate) {
        double accruals = 0.;
        List<CashFlow> flows = getCashFlow(securityMasterData.getCashFlows());
        accruals = bondPricer.calculateAccruedInterest(flows, accrualDate, securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());
        return accruals;
    }
    
    public double repriceBondForYieldShift(SecurityMasterData securityMasterData, LocalDate officialDate, double ytm, double basisPoints) {

        double yieldShift = basisPoints / 100.;
        ytm += yieldShift;

        Calendar calendar = new Calendar(securityMasterData.getCurrency());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = bondPricer.calculatePrice(getCashFlow(securityMasterData.getCashFlows()), ytm, valuationDate,
                securityMasterData.getAccrualDaycount(), Compounding.COMPOUNDED, securityMasterData.getFrequency());

        return newPrice;
    }

    public double calculatePrice(SecurityMasterData securityMasterData, LocalDate officialDate, YieldCurve yieldCurve) {

        Calendar calendar = new Calendar(securityMasterData.getCurrency());
        LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, securityMasterData.getBusinessDays());

        double newPrice = bondPricer.calculatePrice(getCashFlow(securityMasterData.getCashFlows()), yieldCurve, valuationDate,
                securityMasterData.getAccrualDaycount(), securityMasterData.getFrequency());

        return newPrice;
    }
}
