package org.softcaster.easy_pricer_srv.calc;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_srv.dto.BondPricingRequest;
import org.softcaster.easy_pricer_srv.dto.BondPricingResponse;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.BondInputData;
import org.softcaster.engine.dto.BondOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
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
    private InstrumentQuoteDAO instrumentQuoteDAO;
    @Autowired
    @Qualifier("bondPricer") // Indica a Spring esattamente QUALE bean usare
    private BondPricer bondPricer;

    public BondPricingResponse bondValuation(BondPricingRequest request) {
        BondPricingResponse response = null;
        if (smdDAO != null) {
            SecurityMasterData securityMasterData = smdDAO.findByIsin(request.isin);
            if (securityMasterData != null) {
                BondInputData input = new BondInputData();
                input.setValuationDate(LocalDate.now()/*CalendarHelper.getNextBusinessDate(request.referenceDate,
                        securityMasterData.getCalendar(), securityMasterData.getBusinessDays())*/);
                input.setSpotPrice(request.referencePrice);
                input.setFrequency(Frequency.fromCode(securityMasterData.getFrequency().getCode()));
                input.setDaycount(DaycountBasis.fromCode(securityMasterData.getDaycount().getCode()));
                input.setCompounding(Compounding.COMPOUNDED);

                //dbProvider.setYcCode(request.getYieldCurve());
                //dbProvider.refresh(request.getReferenceDate());
                if (!securityMasterData.getCashFlows().isEmpty()) {
                    List<CashFlow> flows = new ArrayList<>();
                    for (org.softcaster.core.data.CashFlowItem item : securityMasterData.getCashFlows()) {
                        CashFlow flow = new CashFlow(
                                item.getStartDate().toLocalDate(),
                                item.getEnddate().toLocalDate(),
                                item.getEnddate().toLocalDate(),
                                item.getAmount(),
                                item.getInterest(),
                                0.
                        );
                        flows.add(flow);
                    }
                    input.setFlows(flows);
                }

                BondOutputData output = bondPricer.calculate(input);
                if (output != null) {
                    response = new BondPricingResponse();
                    response.accruedInterest = output.getAccruedInterest();
                    response.macaulayDuration = 0.;
                    response.yieldToMaturity = output.getYtm();
                    response.modifiedDuration = output.getModifiedDuration();
                    response.convexity = 0.;
                    response.presentValue = 0.;
                    response.yieldToMaturityPV = 0.;
                }
            }
        }

        return response;
    }
    
    public List<BondOutputData> bondsValuation() {
        List<BondOutputData> bondList = new ArrayList<>();
        BondPricingRequest request = new BondPricingRequest();
        org.softcaster.commons.types.Date referenceDate = new org.softcaster.commons.types.Date();
        //dbProvider.refresh(referenceDate.sqlDate());

        // Lista bond su db
        BondOutputData output = null;
        List<SecurityMasterData> securitiesList = smdDAO.findAll();
        for (SecurityMasterData bond : securitiesList) {
            /*
            request.isin = bond.getIsin();
            request.referenceDate = referenceDate.sqlDate();
            request.referencePrice = 100.getEuroNextProvider().getBondQuote(bond.getIsin(), REQUEST_TYPE.MIDDLE);
            request.fullCalc = false;
            //output = bondValuation(request);
            output.setIsin(bond.getIsin());
            output.setMaturity(bond.getMaturityDate());
            // Abilitare solo se fullCalc
            double cleanPriceTh = 0; //output.getPresentValue() - output.getAccruedInterest();
            output.setPresentValue(cleanPriceTh);
            output.setCleanPrice(request.referencePrice);
            bondList.add(output);
            */
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
        //MarketDataService mds = MarketDataService.getInstance();
        //mds.updateBondPrice(tokenList, null);

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
