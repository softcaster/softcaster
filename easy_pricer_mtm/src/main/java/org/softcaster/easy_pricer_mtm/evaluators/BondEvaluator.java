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
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;
import org.softcaster.engine.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    /**
     * Note a tasso fisso.
     */
    @Component("XRN")
    public static class FixedRateNote extends BondEvaluator {
    }

    /**
     * BOT e titoli zero coupon a breve.
     */
    @Component("BLL")
    public static class Bill extends BondEvaluator {
    }

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

                    // ultimi giorni di vita: la data di regolamento (official + business days)
                    // e' gia' la scadenza, non restano flussi per rendimento e duration. Il
                    // titolo vale il prezzo di mercato, il rimborso lo fa lo step MATURITY
                    LocalDate settlementDate = new org.softcaster.easy_pricer_mds_core.Calendar(smd.getCurrencyList())
                            .getNextBusinessDate(mtmHelper.getOfficialDate(), smd.getBusinessDays() == null ? 0 : smd.getBusinessDays());
                    if (smd.getMaturityDate() != null && !settlementDate.isBefore(smd.getMaturityDate().toLocalDate())) {
                        if (mktPrice <= 0.) {
                            throw new MtmException("Missing quote for " + smd.getCode());
                        }
                        currentValuation.setMarketPrice(mktPrice);
                        currentValuation.setTheoreticalPrice(mktPrice);
                        currentValuation.setYtm(0.);
                        currentValuation.setDuration(0.);
                        currentValuation.setModDuration(0.);
                        currentValuation.setAccruedInterest(0.);
                        currentValuation.setValuationDate(settlementDate);
                        currentValuation.setDv01(0.);
                        return currentValuation;
                    }

                    XRBResult output = calculate(smd, mktPrice, mtmHelper.getOfficialDate());
                    if (output != null) {
                        // prezzo secco, come il prezzo medio della posizione (BondTxnProcessor):
                        // il rateo e' a parte (accruedInterest) e la contabilita' lo registra
                        // con gli eventi ACCRUAL, quindi non deve entrare nell'unrealized
                        currentValuation.setMarketPrice(mktPrice);
                        currentValuation.setYtm(output.analytics().yield());
                        currentValuation.setDuration(output.analytics().modifiedDuration());
                        currentValuation.setModDuration(output.analytics().modifiedDuration());
                        currentValuation.setAccruedInterest(output.analytics().accruedInterest());
                        currentValuation.setTheoreticalPrice(mktPrice);
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
            } else {
                // mai saltare in silenzio: la posizione resterebbe al prezzo precedente
                throw new MtmException(masterData.getCode() + " is not a security");
            }
        }
    }
}
