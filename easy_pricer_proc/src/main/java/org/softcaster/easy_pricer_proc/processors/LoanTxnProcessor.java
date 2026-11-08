package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.hibernate.Hibernate;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.LoanMasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.dto.LoanResult;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Deal su un mutuo OTC: la registrazione e' per asset class, con una
 * sottoclasse per tipo di tasso (XRM fisso, FRM variabile).
 *
 * Ogni contratto e' un'anagrafica: quantita' = debito residuo alla data
 * operazione, prezzo = percentuale del residuo (100 dal deal capture); BUY =
 * la banca presta, SELL = la banca prende a prestito (es. deposito multi
 * flusso). Per realized / unrealized si usa il prezzo unitario (prezzo / 100:
 * il multiplier dei mutui e' 1). Rateo, tasso interno, duration da
 * LoanCalculator.dealValuation sul piano salvato, al prezzo del deal e alla
 * data di regolamento (lo scambio di cassa: per un'erogazione futura e' la
 * data di erogazione).
 *
 * Componenti, solo all'inserimento del deal (PENDING / RESTARTING): rateo
 * (BOND_ACCRUAL, se diverso da zero), spese all'erogazione (LOAN_FEE, importo
 * positivo, incassate per BUY e pagate per SELL; solo se il deal non e'
 * successivo all'erogazione) ed EFFECTIVE_INTEREST_RATE per il costo
 * ammortizzato, che le comprende. Rimborsi delle rate e
 * revisioni del tasso variabile sono eventi del ciclo di vita, gestiti a
 * parte.
 */
public abstract class LoanTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Autowired
    private LoanCalculator loanCalculator;
    @Autowired
    private LoanMasterDataDAO loanMasterDataDAO;

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        if (!(Hibernate.unproxy(txn.getMasterData()) instanceof LoanMasterData)) {
            throw new TxnProcessingException("Invalid processor: " + txn.getMasterData().getCode() + " is not a loan");
        }

        // mutuo con piano e spese
        LoanMasterData lmd = loanMasterDataDAO.getById(txn.getMasterData().getIdMasterData());
        LocalDate tradeDate = txn.getTradeDate().toLocalDate();

        // Rimborso della quota capitale di una rata (LoanRepaymentService): alla pari, in
        // data di scadenza, senza rateo ne' spese. Non si valuta: dopo l'ultima rata il
        // mutuo non ha piu' flussi
        if (txn.getTxnOrigin() == TxnOrigin.EOD) {
            super.process(positionInput(lmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);
            return;
        }

        LocalDate settlementDate = txn.getSettlement() != null ? txn.getSettlement().toLocalDate() : tradeDate;
        LoanResult result;
        try {
            result = loanCalculator.dealValuation(lmd, settlementDate, txn.getPrice());
        } catch (RuntimeException e) {
            throw new TxnProcessingException("Valuation of " + lmd.getCode() + " failed: " + e.getMessage());
        }

        double unitPrice = txn.getPrice() / 100.;
        ProcInputData input = positionInput(lmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide());
        super.process(input, position);

        // rateo della quota di capitale negoziata
        double quantity = input.getQuantity();
        double outstanding = result.outstandingPrincipal();
        double accruals = outstanding > 0 ? result.analytics().accruedInterest() * quantity / outstanding : 0.;
        switch (input.getSide()) {
            case BUY -> position.setBuyAccrual(position.getBuyAccrual() + accruals);
            case SELL -> position.setSellAccrual(position.getSellAccrual() + accruals);
        }

        if (txn.getTxnStatusPreElab() == TxnStatus.PENDING || txn.getTxnStatusPreElab() == TxnStatus.RESTARTING) {
            if (accruals != 0.) {
                FinancialTxnComponent component = new FinancialTxnComponent();
                component.setCurrency(lmd.getCurrency());
                component.setDescription("Loan accruals txn: " + txn.getIdFinancialTxn());
                component.setComponentType(TxnComponentType.BOND_ACCRUAL);
                component.setAmount(BigDecimal.valueOf(accruals));
                txn.addTxnComponent(component);
            }

            // spese all'erogazione, per la quota di capitale negoziata: le incassa chi presta
            double fees = lmd.upfrontFees() * txn.getQuantity() / lmd.getPrincipalAmount();
            if (fees != 0. && !settlementDate.isAfter(lmd.getIssueDate().toLocalDate())) {
                FinancialTxnComponent fee = new FinancialTxnComponent();
                fee.setCurrency(lmd.getCurrency());
                fee.setDescription("Loan fees " + (txn.getTxnSide() == TxnSide.BUY ? "received" : "paid")
                        + " txn: " + txn.getIdFinancialTxn());
                fee.setComponentType(TxnComponentType.LOAN_FEE);
                fee.setAmount(BigDecimal.valueOf(fees));
                txn.addTxnComponent(fee);
            }

            FinancialTxnComponent component = new FinancialTxnComponent();
            component.setCurrency(lmd.getCurrency());
            component.setDescription("Effective Interest Rate txn: " + txn.getIdFinancialTxn());
            component.setComponentType(TxnComponentType.EFFECTIVE_INTEREST_RATE);
            component.setAmount(BigDecimal.valueOf(result.analytics().yield()));
            txn.addTxnComponent(component);
        }

        position.setYtm(result.analytics().yield());
        position.setModDuration(result.analytics().modifiedDuration());
        position.setDuration(result.analytics().modifiedDuration());
        LocalDate maturity = lmd.getMaturityDate().toLocalDate();
        position.setTimeToMaturity(lmd.getDaycount().calculate(tradeDate, maturity, lmd.getFrequency()));
        // sulla posizione prezzo unitario, come il mark di AbstractTxnProcessor
        position.setTheoreticalPrice(unitPrice);
        position.setMarketPrice(unitPrice);
    }

    @Override
    protected ProcInputData positionInput(org.softcaster.core.data.MasterData masterData, double quantity, double price, TxnSide side) {
        ProcInputData input = new ProcInputData();
        input.setPrice(price / 100.);
        input.setQuantity(quantity);
        input.setSide(side);
        return input;
    }

    /**
     * Mutuo a tasso fisso.
     */
    @Component("XRM")
    public static class FixedRateLoan extends LoanTxnProcessor {
    }

    /**
     * Mutuo a tasso variabile.
     */
    @Component("FRM")
    public static class FloatingRateLoan extends LoanTxnProcessor {
    }
}
