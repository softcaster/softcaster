/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.CTDData;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingRequest;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.services.MtmService;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("BFU")
public class BondFutureEvaluator extends AbstractEvaluator implements IPositionEvaluator {
    
    private static final Logger log = LoggerFactory.getLogger(MtmService.class);

    @Autowired
    @Qualifier("bondForwardCalculator")
    private BondForwardCalculator bondForwardCalculator;

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {
        Integer masterDataId = masterData.getIdMasterData();

        // Chiamata thread-safe personalizzata
        InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {

            // Controlliamo se MasterData ha già una valutazione caricata dal database
            InstrumentValuation currentValuation = masterData.getInstrumentValuation();

            if (currentValuation == null) {
                // Se non esiste, la creiamo da zero (succederà solo la primissima volta)
                currentValuation = new InstrumentValuation();
                currentValuation.setMasterData(masterData);
                // Impostiamo l'ID esatto dell'anagrafica per renderlo immediatamente "not transient"
                currentValuation.setInstrumentValuationId(masterData.getIdMasterData());
                masterData.setInstrumentValuation(currentValuation); // Aggiorna il lato inverso
            }
            return currentValuation;
        });

        List<Currency> currencies = masterData.getCurrencyList();
        Calendar calendar = new Calendar(currencies);
        LocalDate valuationDate = calendar.getNextBusinessDate(mtmHelper.getOfficialDate(), masterData.getBusinessDays());
        double mktPrice = mtmHelper.getSpotPrice(masterData.getIdMasterData(), RequestType.BID);
        valuation.setTheoreticalPrice(mktPrice);
        valuation.setMarketPrice(mktPrice);
        valuation.setYtm(0.);
        valuation.setDuration(0.);
        valuation.setModDuration(0.);
        valuation.setAccruedInterest(0.);
        setCalculated(true);

        valuation.setValuationDate(valuationDate);

        // Allinea i campi della singola posizione leggendoli dall'oggetto condiviso
        position.setMarketPrice(valuation.getMarketPrice());
        position.setTheoreticalPrice(valuation.getTheoreticalPrice());

        // Calcola il P&L non realizzato (questo è specifico della singola posizione!)
        double unrealized = calcUnrealizedPL(valuation.getMarketPrice() * masterData.getMultiplier(), position);
        position.setUnrealizedPnl(unrealized);

        // Aggiorna il legame bidirezionale nell'anagrafica (opzionale, utile per JPA)
        masterData.setInstrumentValuation(valuation);

        ForwardPricingRequest request = new ForwardPricingRequest();
        request.isin = masterData.getCode();
        request.referencePrice = mktPrice;
        request.referenceDate = java.sql.Date.valueOf(mtmHelper.getOfficialDate());
        request.domesticRate = 0.02182;
        CTDData cTDData = bondForwardCalculator.getCTD(request);
        log.info(cTDData.underlyingIsin);
    }
}
