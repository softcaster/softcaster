package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.LoanMasterDataDAO;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.dto.LoanResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MtM di un mutuo OTC (mark to model): LoanCalculator sul piano salvato, con
 * la curva DISCOUNT di ac_dc_bcy_link per (asset class, valuta), senza spread
 * di credito. Senza curva la valutazione fallisce con MtmException.
 *
 * instrument_valuation, come per i titoli, per 100 di debito residuo: prezzo
 * di mercato = teorico = fair value secco (rate future scontate meno rateo),
 * rateo, DV01; yield e duration del fair value.
 *
 * Posizione: quantita' = debito residuo (i rimborsi delle rate sono deal EOD
 * alla pari che la riducono), prezzo unitario come nel processor: mark = fair
 * value secco / 100. Il MtM non e' contabilizzato (mutui al costo
 * ammortizzato: le asset class XRM / FRM non sono in mtm.eod.asset-classes).
 */
public abstract class LoanEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Autowired
    private LoanCalculator loanCalculator;
    @Autowired
    private LoanMasterDataDAO loanMasterDataDAO;

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {
        Integer masterDataId = masterData.getIdMasterData();
        LocalDate officialDate = mtmHelper.getOfficialDate();

        // mutuo con piano e spese
        LoanMasterData lmd;
        try {
            lmd = loanMasterDataDAO.getById(masterDataId);
        } catch (RuntimeException e) {
            throw new MtmException(masterData.getCode() + " is not a loan: " + e.getMessage());
        }

        InstrumentValuation valuation = context.computeIfAbsentThreadSafe(masterDataId, () -> {

            InstrumentValuation currentValuation = masterData.getInstrumentValuation();
            if (currentValuation == null) {
                currentValuation = new InstrumentValuation();
                currentValuation.setMasterData(masterData);
                currentValuation.setInstrumentValuationId(masterData.getIdMasterData());
                masterData.setInstrumentValuation(currentValuation);
            }

            LoanResult result;
            try {
                result = loanCalculator.valuation(loanCalculator.getLoanInput(lmd, LoanCalculator.fromItems(lmd),
                        officialDate, null, null, null));
            } catch (RuntimeException e) {
                throw new MtmException("Valuation of " + masterData.getCode() + " failed: " + e.getMessage());
            }

            // per 100 di debito residuo; mutuo estinto: tutto a zero
            double outstanding = result.outstandingPrincipal();
            double per100 = outstanding > 0 ? 100. / outstanding : 0.;
            double accrued = result.analytics().accruedInterest();
            double cleanPrice = (result.analytics().dirtyPrice() - accrued) * per100;

            currentValuation.setMarketPrice(cleanPrice);
            currentValuation.setTheoreticalPrice(cleanPrice);
            currentValuation.setYtm(result.analytics().yield());
            currentValuation.setDuration(result.analytics().modifiedDuration());
            currentValuation.setModDuration(result.analytics().modifiedDuration());
            currentValuation.setAccruedInterest(accrued * per100);
            currentValuation.setDv01(result.analytics().dv01() * per100);
            currentValuation.setValuationDate(officialDate);
            return currentValuation;
        });

        // prezzo unitario sul debito residuo: i rimborsi delle rate sono deal EOD
        // (LoanRepaymentService) che riducono la quantita' in posizione insieme al residuo
        double mark = valuation.getMarketPrice() / 100.;

        position.setMarketPrice(mark);
        position.setTheoreticalPrice(mark);
        position.setYtm(valuation.getYtm());
        position.setDuration(valuation.getDuration());
        position.setModDuration(valuation.getModDuration());
        position.setUnrealizedPnl(calcUnrealizedPL(mark, position));

        masterData.setInstrumentValuation(valuation);
    }

    /**
     * Mutuo a tasso fisso.
     */
    @Component("XRM")
    public static class FixedRateLoan extends LoanEvaluator {
    }

    /**
     * Mutuo a tasso variabile: al tasso corrente su tutte le rate, finche' la
     * proiezione sulla curva dell'indice non c'e'.
     */
    @Component("FRM")
    public static class FloatingRateLoan extends LoanEvaluator {
    }
}
