package org.softcaster.easy_pricer_mtm.evaluators;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.OptionMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.engine.dto.OptionInput;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * MtM di una serie di opzioni, per tutte le famiglie: la registrazione e' per
 * asset class, con una sottoclasse per famiglia (FFO, BFO, FXO, BDO).
 *
 * Prezzo di mercato = quotazione della serie (premio unitario). Prezzo
 * teorico = OptionCalculator con la volatilita' della superficie del
 * sottostante (scadenza e strike della serie) e curve di default; senza
 * superficie, o se il calcolo non riesce (es. opzione su titolo senza repo),
 * il teorico e' il prezzo di mercato. Senza quotazione si valuta al teorico
 * (mark to model), se c'e'.
 *
 * Opzione scaduta (scadenza precedente alla data ufficiale): la quotazione di
 * solito non c'e' piu', si usa il valore intrinseco sul prezzo del
 * sottostante (future, cambio, prezzo secco del titolo), 0 se fuori dal
 * denaro, finche' esercizio e scadenza non chiudono la posizione.
 */
public abstract class OptionEvaluator extends AbstractEvaluator implements IPositionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(OptionEvaluator.class);

    @Autowired
    private OptionCalculator optionCalculator;

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper, ValuationContext context) {
        if (!(Hibernate.unproxy(masterData) instanceof OptionMasterData omd)) {
            throw new MtmException(masterData.getCode() + " non e' un'opzione");
        }
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

            List<Currency> currencies = masterData.getCurrencyList();
            Calendar calendar = new Calendar(currencies);
            LocalDate officialDate = mtmHelper.getOfficialDate();
            LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, masterData.getBusinessDays());

            double mktPrice;
            Double theoretical = null;
            LocalDate expiry = omd.getExpiryLocalDate();
            if (expiry != null && expiry.isBefore(officialDate)) {
                mktPrice = intrinsicValue(omd, mtmHelper);
                log.warn("Opzione {} scaduta il {}: valutata al valore intrinseco {}", omd.getCode(), expiry, mktPrice);
            } else {
                theoretical = theoreticalPrice(omd, officialDate);
                mktPrice = mtmHelper.getSpotPrice(masterDataId, RequestType.BID);
                if (mktPrice <= 0.) {
                    if (theoretical == null) {
                        throw new MtmException("Quotazione e superficie di volatilita' mancanti per " + masterData.getCode());
                    }
                    log.warn("Quotazione mancante per {}: valutata al prezzo teorico {}", omd.getCode(), theoretical);
                    mktPrice = theoretical;
                }
            }

            currentValuation.setTheoreticalPrice(theoretical != null ? theoretical : mktPrice);
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

        // quantita' della posizione gia' in contract value (processor): prezzo x multiplier
        double unrealized = calcUnrealizedPL(valuation.getMarketPrice() * masterData.getMultiplier(), position);
        position.setUnrealizedPnl(unrealized);

        // Aggiorna il legame bidirezionale nell'anagrafica (opzionale, utile per JPA)
        masterData.setInstrumentValuation(valuation);
    }

    /**
     * Premio unitario teorico con la volatilita' della superficie, oppure null
     * (nessuna superficie o calcolo non riuscito).
     */
    private Double theoreticalPrice(OptionMasterData omd, LocalDate officialDate) {
        try {
            Optional<Double> vol = optionCalculator.surfaceVolatility(omd.getCode(), officialDate);
            if (vol.isEmpty()) {
                return null;
            }
            OptionInput input = optionCalculator.getOptionInput(omd.getCode(), officialDate, null, null, null, null, vol.get());
            return optionCalculator.valuation(input).price();
        } catch (RuntimeException e) {
            log.warn("Prezzo teorico di {} non calcolato: {}", omd.getCode(), e.getMessage());
            return null;
        }
    }

    /**
     * Valore intrinseco sul prezzo corrente del sottostante.
     */
    private static double intrinsicValue(OptionMasterData omd, IMtmDataHelper mtmHelper) {
        MasterData underlying = omd.getUnderlying();
        if (underlying == null || omd.getStrike() == null) {
            throw new MtmException("Sottostante o strike mancanti per l'opzione scaduta " + omd.getCode());
        }
        // l'id di un proxy LAZY si legge senza inizializzarlo
        double price = mtmHelper.getSpotPrice(underlying.getIdMasterData(), RequestType.BID);
        if (price <= 0.) {
            throw new MtmException("Quotazione del sottostante mancante per l'opzione scaduta " + omd.getCode());
        }
        double strike = omd.getStrike();
        return omd.getOptionType() == OptionType.CALL ? Math.max(price - strike, 0.) : Math.max(strike - price, 0.);
    }

    /**
     * Opzioni su FX future.
     */
    @Component("FFO")
    public static class FxFutureOption extends OptionEvaluator {
    }

    /**
     * Opzioni su bond future.
     */
    @Component("BFO")
    public static class BondFutureOption extends OptionEvaluator {
    }

    /**
     * Opzioni su cambio.
     */
    @Component("FXO")
    public static class FxOption extends OptionEvaluator {
    }

    /**
     * Opzioni su titolo.
     */
    @Component("BDO")
    public static class BondOption extends OptionEvaluator {
    }
}
