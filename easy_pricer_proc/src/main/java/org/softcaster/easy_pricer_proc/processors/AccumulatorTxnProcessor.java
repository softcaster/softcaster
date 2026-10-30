package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.stereotype.Component;

/**
 * Deal su un accumulator / decumulator OTC: la registrazione e' per asset
 * class, con una sottoclasse per sottostante (FXA, CMA).
 *
 * Ogni contratto e' un'anagrafica: quantita' = contratti (1 dal deal
 * capture), prezzo = premio iniziale per contratto nella valuta di
 * regolamento; vendita allo scoperto ammessa (contratto venduto al cliente).
 *
 * Componenti, solo all'inserimento del deal (PENDING / RESTARTING): premio
 * iniziale diverso da zero -> OPTION_PREMIUM, importo positivo; il verso
 * (pagato / incassato) dipende dal lato e dal segno del premio. Fixing,
 * regolamenti periodici e knock-out sono eventi del ciclo di vita, gestiti a
 * parte.
 */
public abstract class AccumulatorTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        if (!(Hibernate.unproxy(txn.getMasterData()) instanceof AccumulatorMasterData amd)) {
            throw new TxnProcessingException("Invalid processor: " + txn.getMasterData().getCode() + " non e' un accumulator");
        }

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice());
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatusPreElab());

        super.process(input, position);

        if (txn.getTxnStatusPreElab() != TxnStatus.PENDING && txn.getTxnStatusPreElab() != TxnStatus.RESTARTING) {
            return;
        }

        double amount = txn.getPrice() * txn.getQuantity();
        if (amount != 0.) {
            // chi compra paga il premio; un premio negativo lo incassa
            boolean paid = (txn.getTxnSide() == TxnSide.BUY) == (amount > 0);
            FinancialTxnComponent premium = new FinancialTxnComponent();
            premium.setCurrency(amd.getCurrency());
            premium.setDescription("Accumulator premium " + (paid ? "paid" : "received")
                    + " txn: " + txn.getIdFinancialTxn());
            premium.setComponentType(TxnComponentType.OPTION_PREMIUM);
            premium.setAmount(BigDecimal.valueOf(Math.abs(amount)));
            txn.addTxnComponent(premium);
        }
    }

    /**
     * Accumulator su cambio.
     */
    @Component("FXA")
    public static class FxAccumulator extends AccumulatorTxnProcessor {
    }

    /**
     * Accumulator su commodity.
     */
    @Component("CMA")
    public static class CommodityAccumulator extends AccumulatorTxnProcessor {
    }
}
