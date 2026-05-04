package org.softcaster.easy_pricer_srv.calc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.easy_pricer_core.data.InstrumentQuote;
import org.softcaster.easy_pricer_core.data.InstrumentQuoteDAO;
import org.softcaster.easy_pricer_core.data.SecurityMasterData;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_srv.dto.BondPricingRequest;
import org.softcaster.easy_pricer_srv.dto.BondPricingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.fixedincome.BondCalcInputData;
import ph.alephzero.finance.products.fixedincome.BondCalcOutputData;

/**
 *
 * @author ep
 */
@Service("bondCalculator")
public class BondCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    private InstrumentQuoteDAO instrumentQuoteDAO;

    public BondPricingResponse bondValuation(BondPricingRequest request) {
        BondPricingResponse response = null;
        if (smdDAO != null) {
            SecurityMasterData securityMasterData = smdDAO.findByIsin(request.isin);
            if (securityMasterData != null) {
                BondCalcInputData input = new BondCalcInputData();
                input.setSettlement(CalendarHelper.getNextBusinessDate(request.referenceDate,
                        securityMasterData.getCalendar(), securityMasterData.getBusinessDays()));
                input.setCurrentPrice(request.referencePrice);
                input.setIssue(securityMasterData.getIssueDate());
                input.setFirstCoupon(securityMasterData.getFirstCouponPaymentDate());
                input.setMaturity(securityMasterData.getMaturityDate());
                input.setLastCoupon(null);
                input.setFrequency(securityMasterData.getFrequency().getYearFraction());
                input.setIssuePrice(securityMasterData.getIssuePrice());
                input.setRedemptionPrice(securityMasterData.getRedempionPrice());
                input.setCouponRate(securityMasterData.getInterestRate() / 100.);
                input.setBasis(DayCountBasis.ACT_ACT);

                //dbProvider.setYcCode(request.getYieldCurve());
                //dbProvider.refresh(request.getReferenceDate());
                if (!securityMasterData.getCashFlows().isEmpty()) {
                    List<ph.alephzero.finance.cashflows.CashFlowItem> cashFlows = new ArrayList<>();
                    for (org.softcaster.easy_pricer_core.data.CashFlowItem item : securityMasterData.getCashFlows()) {
                        ph.alephzero.finance.cashflows.CashFlowItem cashFlowItem = new ph.alephzero.finance.cashflows.CashFlowItem();
                        cashFlowItem.setStart(item.getStartDate());
                        cashFlowItem.setEnd(item.getEnddate());
                        cashFlowItem.setInterest(item.getInterest());
                        cashFlowItem.setAmount(item.getAmount());
                        cashFlowItem.setDiscountFactors(0.1/*dbProvider.getMoneyMarketRate("", REQUEST_TYPE.BID, item.getEnddate())*/);
                        cashFlows.add(cashFlowItem);
                    }
                    input.setCashFlows(cashFlows);
                }

                input.setFullCalc(request.fullCalc);
                ph.alephzero.finance.products.fixedincome.BondCalculator calculator = new ph.alephzero.finance.products.fixedincome.BondCalculator();
                BondCalcOutputData output = calculator.bondValuation(input);
                if (output != null) {
                    response = new BondPricingResponse();
                    response.accruedInterest = output.getAccruedInterest();
                    response.macaulayDuration = output.getDurationMacaulay();
                    response.yieldToMaturity = output.getYieldToMaturity();
                    response.modifiedDuration = output.getDurationModified();
                    response.convexity = output.getConvexity();
                    response.presentValue = 0.;
                    response.yieldToMaturityPV = 0.;
                }
            }
        }

        return response;
    }
    
    public List<BondCalcOutputData> bondsValuation() {
        List<BondCalcOutputData> bondList = new ArrayList<>();
        BondPricingRequest request = new BondPricingRequest();
        org.softcaster.commons.types.Date referenceDate = new org.softcaster.commons.types.Date();
        //dbProvider.refresh(referenceDate.sqlDate());

        // Lista bond su db
        BondCalcOutputData output = null;
        List<SecurityMasterData> securitiesList = smdDAO.findAll();
        for (SecurityMasterData bond : securitiesList) {
            request.isin = bond.getIsin();
            request.referenceDate = referenceDate.sqlDate();
            request.referencePrice = 100./*getEuroNextProvider().getBondQuote(bond.getIsin(), REQUEST_TYPE.MIDDLE)*/;
            request.fullCalc = false;
            //output = bondValuation(request);
            output.setIsin(bond.getIsin());
            output.setMaturity(bond.getMaturityDate());
            // Abilitare solo se fullCalc
            double cleanPriceTh = 0; //output.getPresentValue() - output.getAccruedInterest();
            output.setPresentValue(cleanPriceTh);
            output.setCleanPrice(request.referencePrice);
            bondList.add(output);
        }

        return bondList;
    }

    public BondPricingResponse bondValuation(String isin) {

        // Leggo lista pairs anagrafiche
        List<InstrumentQuote> iqList = instrumentQuoteDAO.findByAssetClass("XRB");
        if (iqList.isEmpty()) {
            return null;
        }

        // Aggiorno service
        Map<String, List<String>> tokenList = new HashMap<>();
        for (InstrumentQuote quote : iqList) {
            tokenList.computeIfAbsent(quote.getProvider(), k -> new ArrayList<>()).add(quote.getCode());
        }
        MarketDataService mds = MarketDataService.getInstance();
        mds.updateBondPrice(tokenList, null);

        BondPricingRequest request = new BondPricingRequest();
        /*
        request.isin = isin;
        request.setReferencePrice(mds.getSpotPrice(isin + "-MOTX", REQUEST_TYPE.BID));
        request.setReferenceDate(new Date().sqlDate());
        request.setFullCalc(false);
         */
        return bondValuation(request);
    }
}
