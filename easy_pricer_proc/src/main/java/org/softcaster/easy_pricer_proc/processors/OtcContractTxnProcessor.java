package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.OtcOptionMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.stereotype.Component;

/**
 * Deal su un contratto OTC con un'anagrafica per contratto: accumulator /
 * decumulator (FXA, CMA) e opzioni OTC (OFX, OCM). La registrazione e' per
 * asset class, con una sottoclasse per ciascuna.
 *
 * Quantita' = contratti (1 dal deal capture), prezzo = premio per contratto
 * nella valuta di regolamento (per un'opzione il premio totale sul
 * nozionale); vendita allo scoperto ammessa (contratto venduto al cliente).
 *
 * Componenti, solo all'inserimento del deal (PENDING / RESTARTING): premio
 * diverso da zero -> OPTION_PREMIUM, importo positivo; il verso (pagato /
 * incassato) dipende dal lato e dal segno del premio (un accumulator puo'
 * avere premio negativo). Anche i deal EOD di chiusura passano di qui:
 * all'esercizio di un'opzione il valore intrinseco e' il premio incassato da
 * chi era lungo. Fixing, regolamenti periodici, knock-out e scadenze sono
 * eventi del ciclo di vita, gestiti a parte.
 */
public abstract class OtcContractTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    /**
     * Anagrafica attesa dal processor e nome del contratto nei messaggi.
     */
    protected abstract boolean accepts(MasterData masterData);

    protected abstract String contractName();

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        MasterData md = (MasterData) Hibernate.unproxy(txn.getMasterData());
        if (!accepts(md)) {
            throw new TxnProcessingException("Invalid processor: " + md.getCode() + " is not an " + contractName());
        }

        super.process(positionInput(md, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);

        if (txn.getTxnStatusPreElab() != TxnStatus.PENDING && txn.getTxnStatusPreElab() != TxnStatus.RESTARTING) {
            return;
        }

        double amount = txn.getPrice() * txn.getQuantity();
        if (amount != 0.) {
            // chi compra paga il premio; un premio negativo lo incassa
            boolean paid = (txn.getTxnSide() == TxnSide.BUY) == (amount > 0);
            FinancialTxnComponent premium = new FinancialTxnComponent();
            premium.setCurrency(md.getCurrency());
            premium.setDescription(contractName() + " premium " + (paid ? "paid" : "received")
                    + " txn: " + txn.getIdFinancialTxn());
            premium.setComponentType(TxnComponentType.OPTION_PREMIUM);
            premium.setAmount(BigDecimal.valueOf(Math.abs(amount)));
            txn.addTxnComponent(premium);
        }
    }

    @Override
    protected ProcInputData positionInput(MasterData masterData, double quantity, double price, TxnSide side) {
        ProcInputData input = new ProcInputData();
        input.setPrice(price);
        input.setQuantity(quantity);
        input.setSide(side);
        return input;
    }

    private abstract static class Accumulator extends OtcContractTxnProcessor {

        @Override
        protected boolean accepts(MasterData masterData) {
            return masterData instanceof AccumulatorMasterData;
        }

        @Override
        protected String contractName() {
            return "Accumulator";
        }
    }

    private abstract static class OtcOption extends OtcContractTxnProcessor {

        @Override
        protected boolean accepts(MasterData masterData) {
            return masterData instanceof OtcOptionMasterData;
        }

        @Override
        protected String contractName() {
            return "OTC option";
        }
    }

    /**
     * Accumulator su cambio.
     */
    @Component("FXA")
    public static class FxAccumulator extends Accumulator {
    }

    /**
     * Accumulator su commodity.
     */
    @Component("CMA")
    public static class CommodityAccumulator extends Accumulator {
    }

    /**
     * Opzione OTC su cambio.
     */
    @Component("OFX")
    public static class FxOtcOption extends OtcOption {
    }

    /**
     * Opzione OTC su commodity.
     */
    @Component("OCM")
    public static class CommodityOtcOption extends OtcOption {
    }
}
