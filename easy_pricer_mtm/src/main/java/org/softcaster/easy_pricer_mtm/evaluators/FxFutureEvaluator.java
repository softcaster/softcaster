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
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * MtM di una scadenza di FX future: prezzo di mercato = quotazione della
 * scadenza, prezzo teorico = cambio forward alla consegna (spot del
 * sottostante e curve della coppia di valute).
 */
@Component("FFU")
public class FxFutureEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(FxFutureEvaluator.class);

    @Autowired
    @Qualifier("fxForwardCalculator")
    private FxForwardCalculator fxForwardCalculator;

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {
        Integer masterDataId = masterData.getIdMasterData();

        // Chiamata thread-safe personalizzata
        InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {

            // Controlliamo se MasterData ha già  una valutazione caricata dal database
            InstrumentValuation currentValuation = masterData.getInstrumentValuation();

            if (currentValuation == null) {
                // Se non esiste, la creiamo da zero (succederà  solo la primissima volta)
                currentValuation = new InstrumentValuation();
                currentValuation.setMasterData(masterData);
                // Impostiamo l'ID esatto dell'anagrafica per renderlo immediatamente "not transient"
                currentValuation.setInstrumentValuationId(masterData.getIdMasterData());
                masterData.setInstrumentValuation(currentValuation); // Aggiorna il lato inverso
            }
            List<Currency> currencies = masterData.getCurrencyList();
            Calendar calendar = new Calendar(currencies);
            LocalDate valuationDate = calendar.getNextBusinessDate(mtmHelper.getOfficialDate(), masterData.getBusinessDays());
            double mktPrice = mtmHelper.getSpotPrice(masterData.getIdMasterData(), RequestType.BID);
            if (mktPrice <= 0.) {
                throw new MtmException("Quotazione mancante per " + masterData.getCode());
            }
            Double theoreticalPrice = theoreticalPrice((FxFutureMasterData) masterData, mtmHelper);
            currentValuation.setTheoreticalPrice(theoreticalPrice != null ? theoreticalPrice : mktPrice);
            currentValuation.setMarketPrice(mktPrice);
            currentValuation.setYtm(0.);
            currentValuation.setDuration(0.);
            currentValuation.setModDuration(0.);
            currentValuation.setAccruedInterest(0.);
            currentValuation.setValuationDate(valuationDate);

            return currentValuation;
        });

        // Allinea i campi della singola posizione leggendoli dall'oggetto condiviso
        position.setMarketPrice(valuation.getMarketPrice());
        position.setTheoreticalPrice(valuation.getTheoreticalPrice());

        // Calcola il P&L non realizzato (questo è specifico della singola posizione!)
        double unrealized = calcUnrealizedPL(valuation.getMarketPrice() * masterData.getMultiplier(), position);
        position.setUnrealizedPnl(unrealized);

        // Aggiorna il legame bidirezionale nell'anagrafica (opzionale, utile per JPA)
        masterData.setInstrumentValuation(valuation);
    }

    /**
     * Cambio forward alla consegna, oppure null se non calcolabile (scadenza
     * negoziata per l'ultima volta, spot o curve mancanti): l'MtM resta sul
     * prezzo di mercato.
     */
    private Double theoreticalPrice(FxFutureMasterData ffmd, IMtmDataHelper mtmHelper) {
        LocalDate officialDate = mtmHelper.getOfficialDate();
        LocalDate lastTrading = ffmd.getLastTradingLocalDate();
        if (lastTrading != null && lastTrading.isBefore(officialDate)) {
            log.warn("Future {} scaduto il {}: prezzo teorico non calcolato", ffmd.getCode(), lastTrading);
            return null;
        }
        double spot = mtmHelper.getSpotPrice(ffmd.getUnderlying().getCode(), RequestType.BID);
        if (spot <= 0.) {
            log.warn("Spot {} mancante: prezzo teorico di {} non calcolato", ffmd.getUnderlying().getCode(), ffmd.getCode());
            return null;
        }
        try {
            // curve vuote: si usano quelle della coppia di valute del sottostante
            FxForwardInput input = fxForwardCalculator.getFxForwardInput(ffmd.getCode(), officialDate, spot, "", "");
            FxForwardResult result = fxForwardCalculator.fxFwdValuation(input);
            return result.forwardRate();
        } catch (RuntimeException e) {
            log.warn("Prezzo teorico di {} non calcolato: {}", ffmd.getCode(), e.getMessage());
            return null;
        }
    }
}
