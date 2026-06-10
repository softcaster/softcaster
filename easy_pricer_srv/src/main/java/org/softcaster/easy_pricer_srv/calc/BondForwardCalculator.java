/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.DeliverableBonds;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_srv.dto.ForwardPricingResponse;
import org.softcaster.easy_pricer_srv.dto.ForwardPricingRequest;
import org.softcaster.engine.analytics.BondForwardPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.BondForwardInputData;
import org.softcaster.engine.dto.MarketOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

class CTDData {

    public String underlyingIsin = "";
    public List<CashFlow> underliyngCashFlow = null;
    public double cf = 0;
    public LocalDate maturity = null;
    public DaycountBasis accrualDaycount = null;
    public Frequency frequency = null;
    public double cleanSpotPrice = 0;
}

@Service("bondForwardCalculator")
public class BondForwardCalculator {

    @Autowired
    private SecurityMasterDataDAO smdDAO;
    @Autowired
    private BondFutureMasterDataDAO bondFutureMasterDataDAO;
    @Autowired
    private InstrumentQuoteDAO instrumentQuoteDAO;

    @Autowired
    @Qualifier("bondFwdPricer")
    private BondForwardPricer bondForwardPricer;

    public ForwardPricingResponse bondFwdValuation(ForwardPricingRequest request) {

        MarketOutputData output = null;

        CTDData ctdData = getCTD(request);
        if (ctdData != null && !ctdData.underlyingIsin.isBlank()) {

            BondForwardInputData input = new BondForwardInputData();
            input.setSpotPrice(ctdData.cleanSpotPrice);
            input.setValuationDate(request.referenceDate.toLocalDate());
            input.setDaycount(ctdData.accrualDaycount);
            input.setDomesticRate(request.domesticRate);
            input.setForeignRate(request.foreignRate);
            input.setMaturityDate(ctdData.maturity);
            input.setCompounding(Compounding.COMPOUNDED);
            input.setConversionFactor(ctdData.cf);
            input.setUnderliyngCashFlows(ctdData.underliyngCashFlow);

            output = bondForwardPricer.calculateForwardPrice(input);
         }

        ForwardPricingResponse response = null;
        if (output != null) {
            response = new ForwardPricingResponse();
            response.theoreticalPrice = output.getPrice();
            response.ctd = ctdData.underlyingIsin;
        }
        return response;
    }

    private CTDData getCTD(ForwardPricingRequest request) {

        CTDData ctdData = null;
        BondFutureMasterData bfmd = bondFutureMasterDataDAO.findByIsin(request.isin);
        if (bfmd != null) {
            Calendar calendar = new Calendar(bfmd.getCurrency());
            SecurityMasterData smd = null;
            ctdData = new CTDData();
            boolean updateCtdData = true;
            boolean isFirst = true;
            double lastDelta = 0.;
            List<CashFlow> underlyingCashFlow = null;
            for (DeliverableBonds deliverable : bfmd.getDeliverables()) {
                smd = smdDAO.findByIsin(deliverable.getIsin());

                // Titolo non disponibile in anagrafica
                if (smd == null) {
                    continue;
                }

                // 1. Recupero prezzo CLEAN spot dal provider
                InstrumentQuote instrumentQuote = instrumentQuoteDAO.findByMasterDataCode(deliverable.getIsin());
                // Prezzo non disponibile
                if (instrumentQuote == null) {
                    continue;
                }
                double cleanSpotPrice = instrumentQuote.getBid();

                // Data valuta
                LocalDate valuationDate = calendar.getNextBusinessDate(request.referenceDate, smd.getBusinessDays());

                // Cash flow sottostante
                underlyingCashFlow = Utils.convertCashFlow(smd.getCashFlows());

                // Calcolo dei ratei usando i metodi della classe BondForwardPricer
                DaycountBasis accrualDaycount = smd.getAccrualDaycount();
                Frequency frequency = Utils.convertFrequency(smd.getFrequency());
                double spotAccrual = bondForwardPricer.calculateAccrualAtDate(underlyingCashFlow,
                        valuationDate,
                        accrualDaycount,
                        frequency);

                double deliveryAccrual = bondForwardPricer.calculateAccrualAtDate(underlyingCashFlow,
                        bfmd.getMaturityDate().toLocalDate(),
                        accrualDaycount,
                        frequency);

                // Trasformazione in prezzi DIRTY (Prezzi effettivi di scambio monetario)
                double dirtySpotPrice = cleanSpotPrice + spotAccrual;
                double invoicePrice = (request.referencePrice * deliverable.getBondCf()) + deliveryAccrual;

                // Calcolo del Cost of Carry (Interessi di finanziamento sul prezzo dirty spot)
                DaycountBasis fwdDaycount = bfmd.getDaycount();
                double maturityTenor = fwdDaycount.calculate(valuationDate, bfmd.getMaturityDate().toLocalDate(), null);
                double carryCost = dirtySpotPrice * request.domesticRate * maturityTenor;

                // Calcolo delle eventuali cedole intermedie incassate e capitalizzate
                double capitalizedCoupons = bondForwardPricer.getCapitalizedIntermediateCoupons(underlyingCashFlow,
                        valuationDate,
                        request.domesticRate,
                        bfmd.getMaturityDate().toLocalDate(),
                        request.domesticRate,
                        fwdDaycount,
                        Utils.convertCompounding("COMPOUNDED"),
                        frequency);

                // NET BASIS esatta
                double netBasis = dirtySpotPrice + carryCost - capitalizedCoupons - invoicePrice;

                double moneyMarketTenor = DaycountBasis.ACT_360.calculate(valuationDate, bfmd.getMaturityDate().toLocalDate(), null);
                if (moneyMarketTenor > 0.0) {
                    // 2. Calcolo del rendimento totale del periodo (Invoice + Cedole fisiche / Dirty Spot)
                    double totalReturn = (invoicePrice + capitalizedCoupons) / dirtySpotPrice;

                    // 3. Annualizzazione corretta dividendo per la frazione d'anno ACT/360
                    double irr = (totalReturn - 1.0) / moneyMarketTenor;

                    // Stampa i risultati corretti ed eliminando le distorsioni
                    System.out.println("ISIN: " + deliverable.getIsin()
                            + " Clean Price: " + cleanSpotPrice
                            + " -> Net Basis: " + netBasis
                            + " | IRR Normalizzato: " + String.format("%.4f", irr * 100) + "%");
                }

                if (isFirst) {
                    lastDelta = netBasis;
                    isFirst = false;
                } else {
                    // Il CTD è il titolo che MINIMIZZA la Net Basis (ovvero il valore più basso)
                    if (netBasis < lastDelta) {
                        lastDelta = netBasis;
                        updateCtdData = true;
                    }
                }
                if (updateCtdData) {
                    ctdData.underlyingIsin = deliverable.getIsin();
                    ctdData.cf = deliverable.getBondCf();
                    ctdData.underliyngCashFlow = underlyingCashFlow;
                    ctdData.accrualDaycount = accrualDaycount;
                    ctdData.frequency = frequency;
                    ctdData.maturity = bfmd.getMaturityDate().toLocalDate();
                    ctdData.cleanSpotPrice = cleanSpotPrice;
                    updateCtdData = false;
                }
            }
        }

        return ctdData;
    }

}
