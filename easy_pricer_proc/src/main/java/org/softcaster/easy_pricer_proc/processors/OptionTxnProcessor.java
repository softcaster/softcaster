package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.OptionMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.PremiumStyle;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.stereotype.Component;

/**
 * Deal su una serie di opzioni, per tutte le famiglie: la registrazione e'
 * per asset class, con una sottoclasse per famiglia (FFO, BFO, FXO, BDO).
 *
 * Posizione come per i future: quantita' = contratti x contract value, prezzo
 * = premio x multiplier (premio per 100 di nominale sulle opzioni su titolo,
 * multiplier 0,01); vendita allo scoperto ammessa (opzione scritta).
 *
 * Componenti, solo all'inserimento del deal (PENDING / RESTARTING):
 *  - premio "equity-style" (CME, OTC): OPTION_PREMIUM = premio x multiplier x
 *    contratti x contract value, pagato (BUY) o incassato (SELL); importo
 *    positivo, il verso e' il lato del deal. Margine iniziale solo sulle
 *    opzioni vendute, se il prodotto ne ha uno (opzioni quotate);
 *  - premio "futures-style" (Eurex): nessun esborso di premio, margine iniziale
 *    su entrambi i lati come per i future.
 */
public abstract class OptionTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        if (!(Hibernate.unproxy(txn.getMasterData()) instanceof OptionMasterData omd)) {
            throw new TxnProcessingException("Invalid processor: " + txn.getMasterData().getCode() + " non e' un'opzione");
        }
        if (omd.getContractValue() == null || omd.getMultiplier() == null || omd.getPremiumStyle() == null) {
            throw new TxnProcessingException("Contract value, multiplier o stile del premio mancanti per " + omd.getCode());
        }

        ProcInputData input = positionInput(omd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide());
        super.process(input, position);

        if (txn.getTxnStatusPreElab() != TxnStatus.PENDING && txn.getTxnStatusPreElab() != TxnStatus.RESTARTING) {
            return;
        }

        boolean futuresStyle = omd.getPremiumStyle() == PremiumStyle.FUTURES_STYLE;
        if (!futuresStyle && input.getPrice() * input.getQuantity() != 0.) {
            FinancialTxnComponent premium = new FinancialTxnComponent();
            premium.setCurrency(omd.getCurrency());
            premium.setDescription("Option premium " + (txn.getTxnSide() == TxnSide.BUY ? "paid" : "received")
                    + " txn: " + txn.getIdFinancialTxn());
            premium.setComponentType(TxnComponentType.OPTION_PREMIUM);
            premium.setAmount(BigDecimal.valueOf(input.getPrice() * input.getQuantity()));
            txn.addTxnComponent(premium);
        }

        // nessun margine sui deal di scadenza (EOD): chiudono la posizione
        boolean margined = !isEodDeal(txn) && (futuresStyle || txn.getTxnSide() == TxnSide.SELL);
        if (margined && omd.getInitialMargin() != null && omd.getInitialMargin() > 0) {
            FinancialTxnComponent initialMargin = new FinancialTxnComponent();
            initialMargin.setCurrency(omd.getCurrency());
            initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
            initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
            initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * omd.getInitialMargin()));
            txn.addTxnComponent(initialMargin);
        }
    }

    @Override
    protected ProcInputData positionInput(org.softcaster.core.data.MasterData masterData, double quantity, double price, TxnSide side) {
        OptionMasterData omd = (OptionMasterData) Hibernate.unproxy(masterData);
        ProcInputData input = new ProcInputData();
        input.setPrice(price * omd.getMultiplier());
        input.setPriceMultiplier(omd.getMultiplier());
        input.setQuantity(quantity * omd.getContractValue());
        input.setSide(side);
        return input;
    }

    /**
     * Opzioni su FX future.
     */
    @Component("FFO")
    public static class FxFutureOption extends OptionTxnProcessor {
    }

    /**
     * Opzioni su bond future.
     */
    @Component("BFO")
    public static class BondFutureOption extends OptionTxnProcessor {
    }

    /**
     * Opzioni su cambio.
     */
    @Component("FXO")
    public static class FxOption extends OptionTxnProcessor {
    }

    /**
     * Opzioni su titolo.
     */
    @Component("BDO")
    public static class BondOption extends OptionTxnProcessor {
    }
}
