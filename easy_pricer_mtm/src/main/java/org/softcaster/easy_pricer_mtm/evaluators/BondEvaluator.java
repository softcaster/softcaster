/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.provider.enums.RequestType;
import org.springframework.stereotype.Component;

@Component("BOND")
public class BondEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {

        position.initializeMtmFields();
        Integer masterDataId = masterData.getIdMasterData();

        // Chiamata thread-safe personalizzata
        InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {
            InstrumentValuation newValuation = new InstrumentValuation();
            newValuation.setMasterData(masterData);

            double mktPrice = 0.;//mtmHelper.getBondPrice(masterData.getCode());
            double ytm = 0.;//(mktPrice, masterData); // Calcolo pesante protetto da lock
            double duration = 0.;//(mktPrice, ytm, masterData); // Calcolo pesante

            newValuation.setMarketPrice(mktPrice);
            newValuation.setYtm(ytm);
            newValuation.setDuration(duration);
            return newValuation;
        });
        
        // Allinea i campi della singola posizione leggendoli dall'oggetto condiviso
        position.setMarketPrice(valuation.getMarketPrice());
        position.setYtm(valuation.getYtm());
        position.setDuration(valuation.getDuration());

        // Calcola il P&L non realizzato (questo è specifico della singola posizione!)
        double unrealized = calcUnrealizedPL(valuation.getMarketPrice(), position);
        position.setUnrealizedPnl(unrealized);

        // Aggiorna il legame bidirezionale nell'anagrafica (opzionale, utile per JPA)
        masterData.setInstrumentValuation(valuation);
    }
}
