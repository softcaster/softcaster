package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.OtcOptionMasterData;
import org.softcaster.core.data.OtcOptionMasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MtM di un'opzione OTC (mark to model): nessuna quotazione, prezzo di
 * mercato = prezzo teorico = valore del contratto (premio sul nozionale)
 * nella valuta di regolamento per chi lo detiene (OptionCalculator: curve
 * di default, prezzo del sottostante quotato, volatilita' dalla superficie
 * del sottostante alla scadenza e allo strike). Per un contratto venduto la
 * posizione e' corta e il P&L cambia segno.
 *
 * Senza superficie di volatilita' o curve la valutazione fallisce con
 * MtmException.
 */
public abstract class OtcOptionEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    private OtcOptionMasterDataDAO otcOptionMasterDataDAO;

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {
        Integer masterDataId = masterData.getIdMasterData();

        InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {

            InstrumentValuation currentValuation = masterData.getInstrumentValuation();
            if (currentValuation == null) {
                currentValuation = new InstrumentValuation();
                currentValuation.setMasterData(masterData);
                currentValuation.setInstrumentValuationId(masterData.getIdMasterData());
                masterData.setInstrumentValuation(currentValuation);
            }

            LocalDate officialDate = mtmHelper.getOfficialDate();
            double value;
            try {
                OtcOptionMasterData omd = otcOptionMasterDataDAO.getById(masterDataId);
                PricingResponse result = optionCalculator.otcOptionValuation(omd, officialDate, null, null, null, null, null);
                value = result.optionValue;
            } catch (RuntimeException e) {
                throw new MtmException("Valuation of " + masterData.getCode() + " failed: " + e.getMessage());
            }

            currentValuation.setTheoreticalPrice(value);
            currentValuation.setMarketPrice(value);
            currentValuation.setYtm(0.);
            currentValuation.setDuration(0.);
            currentValuation.setModDuration(0.);
            currentValuation.setAccruedInterest(0.);
            currentValuation.setValuationDate(officialDate);
            return currentValuation;
        });

        position.setMarketPrice(valuation.getMarketPrice());
        position.setTheoreticalPrice(valuation.getTheoreticalPrice());

        // prezzo = valore per contratto
        position.setUnrealizedPnl(calcUnrealizedPL(valuation.getMarketPrice(), position));

        masterData.setInstrumentValuation(valuation);
    }

    /**
     * Opzione OTC su cambio.
     */
    @Component("OFX")
    public static class FxOtcOption extends OtcOptionEvaluator {
    }

    /**
     * Opzione OTC su commodity.
     */
    @Component("OCM")
    public static class CommodityOtcOption extends OtcOptionEvaluator {
    }
}
