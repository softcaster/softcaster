/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    @Autowired
    @Qualifier("bondPricer") // Indica a Spring esattamente QUALE bean usare
    private BondPricer bondPricer;

    private XRBOutputData calculate(MasterData masterData, double mktPrice, LocalDate officialDate) {
        XRBOutputData output = null;

        if (masterData != null) {
            if (masterData instanceof SecurityMasterData smd) {

                List<Currency> currencies = smd.getCurrencyList();
                if (currencies != null && !currencies.isEmpty()) {
                    LocalDate valuationDate = null;
                    Calendar calendar = new Calendar(currencies);
                    valuationDate = calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());

                    XRBInputData input = new XRBInputData();
                    input.setValuationDate(valuationDate);
                    input.setReferencePrice(mktPrice);
                    input.setFrequency(Frequency.fromCode(smd.getFrequency().getCode()));
                    input.setDaycount(smd.getAccrualDaycount());
                    input.setCompounding(Compounding.COMPOUNDED);

                    // Cash Flow
                    if (!smd.getCashFlows().isEmpty()) {
                        List<CashFlow> flows = new ArrayList<>();
                        for (org.softcaster.core.data.CashFlowItem item : smd.getCashFlows()) {
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
                    output = bondPricer.calculate(input);
                }
            }
        }

        return output;
    }

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {

        position.initializeMtmFields();
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

        double mktPrice = mtmHelper.getSpotPrice(masterData.getCode(), RequestType.BID);

        XRBOutputData output = calculate(masterData, mktPrice, mtmHelper.getOfficialDate());
        if (output != null) {
            valuation.setMarketPrice(output.getMktPrice());
            valuation.setYtm(output.getYtm());
            valuation.setDuration(output.getDuration());
            valuation.setModDuration(output.getModifiedDuration());
            valuation.setAccruedInterest(output.getAccruedInterest());
            valuation.setTheoreticalPrice(output.getMktPrice());
            valuation.setValuationDate(output.getValuationDate());
            valuation.setDv01(output.getDv01());
        }

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
