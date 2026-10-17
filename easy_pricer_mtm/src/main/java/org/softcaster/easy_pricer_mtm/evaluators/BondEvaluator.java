/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Autowired
    @Qualifier("cashFlowInstrumentCalculator")
    private CashFlowInstrumentCalculator bondCalculator;

    private XRBResult calculate(SecurityMasterData smd, double mktPrice, LocalDate officialDate) {
        XRBInput bondInputData = bondCalculator.getXRBInput(officialDate, mktPrice, smd);
        return bondCalculator.fixedIncomeValuation(bondInputData);
    }

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {

        if (masterData != null) {
            if (masterData instanceof SecurityMasterData smd) {

                position.initializeMtmFields();
                Integer masterDataId = masterData.getIdMasterData();

                // Chiamata thread-safe personalizzata
                InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {

                    // Controlliamo se MasterData ha già una valutazione caricata dal database
                    InstrumentValuation currentValuation = smd.getInstrumentValuation();

                    if (currentValuation == null) {
                        // Se non esiste, la creiamo da zero (succederà solo la primissima volta)
                        currentValuation = new InstrumentValuation();
                        currentValuation.setMasterData(masterData);
                        // Impostiamo l'ID esatto dell'anagrafica per renderlo immediatamente "not transient"
                        currentValuation.setInstrumentValuationId(smd.getIdMasterData());
                        masterData.setInstrumentValuation(currentValuation); // Aggiorna il lato inverso
                    }
                    double mktPrice = mtmHelper.getSpotPrice(masterData.getCode(), RequestType.BID);

                    XRBResult output = calculate(smd, mktPrice, mtmHelper.getOfficialDate());
                    if (output != null) {
                        currentValuation.setMarketPrice(output.analytics().dirtyPrice());
                        currentValuation.setYtm(output.analytics().yield());
                        currentValuation.setDuration(output.analytics().modifiedDuration());
                        currentValuation.setModDuration(output.analytics().modifiedDuration());
                        currentValuation.setAccruedInterest(output.analytics().accruedInterest());
                        currentValuation.setTheoreticalPrice(output.analytics().dirtyPrice());
                        currentValuation.setValuationDate(output.valuationDate());
                        currentValuation.setDv01(output.analytics().dv01());
                    }

                    return currentValuation;
                });

                //double priceYieldUp = bondCalculator.repriceBondForYieldShift(smd, mtmHelper.getOfficialDate(), output.getYtm(), 1.);
                // Allinea i campi della singola posizione leggendoli dall'oggetto condiviso
                position.setMarketPrice(valuation.getMarketPrice());
                position.setTheoreticalPrice(valuation.getMarketPrice());
                position.setYtm(valuation.getYtm());
                position.setDuration(valuation.getDuration());
                position.setModDuration(valuation.getModDuration());

                // Calcola il P&L non realizzato (questo è specifico della singola posizione!)
                double unrealized = calcUnrealizedPL(valuation.getMarketPrice() * masterData.getMultiplier(), position);
                position.setUnrealizedPnl(unrealized);

                // Aggiorna il legame bidirezionale nell'anagrafica (opzionale, utile per JPA)
                masterData.setInstrumentValuation(valuation);
            }
        }
    }
}
