package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.AccumulatorMasterDataDAO;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.AccumulatorCalculator;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.dto.AccumulatorInput;
import org.softcaster.engine.dto.AccumulatorResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MtM di un accumulator / decumulator OTC (mark to model): nessuna
 * quotazione, prezzo di mercato = prezzo teorico = valore del contratto per
 * chi lo detiene (AccumulatorCalculator: curve di default, spot quotato,
 * volatilita' dalla superficie del sottostante). Il valore puo' essere
 * negativo; per un contratto venduto la posizione e' corta e il P&L cambia
 * segno.
 *
 * Senza superficie di volatilita', curve o fixing passati rilevati la
 * valutazione fallisce con MtmException.
 */
public abstract class AccumulatorEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Autowired
    private AccumulatorCalculator accumulatorCalculator;
    @Autowired
    private AccumulatorMasterDataDAO accumulatorMasterDataDAO;

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
                // contratto con calendario dei fixing e sottostante
                AccumulatorMasterData amd = accumulatorMasterDataDAO.getById(masterDataId);
                AccumulatorInput input = accumulatorCalculator.getAccumulatorInput(amd, officialDate, null, null, null, null, null);
                AccumulatorResult result = accumulatorCalculator.valuation(input);
                value = result.value();
            } catch (RuntimeException e) {
                throw new MtmException("Valutazione di " + masterData.getCode() + " non riuscita: " + e.getMessage());
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
     * Accumulator su cambio.
     */
    @Component("FXA")
    public static class FxAccumulator extends AccumulatorEvaluator {
    }

    /**
     * Accumulator su commodity.
     */
    @Component("CMA")
    public static class CommodityAccumulator extends AccumulatorEvaluator {
    }
}
