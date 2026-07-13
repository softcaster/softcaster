/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.TxnComponentType;
import static org.softcaster.engine.enums.TxnSide.BUY;
import static org.softcaster.engine.enums.TxnSide.SELL;
import org.softcaster.engine.enums.TxnStatus;
import static org.softcaster.engine.enums.TxnStatus.PENDING;
import static org.softcaster.engine.enums.TxnStatus.RESTARTING;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Autowired
    @Qualifier("bondPricer")
    private BondPricer bondPricer;

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        SecurityMasterData smd = null;
        if (txn.getMasterData() instanceof SecurityMasterData bond) {
            smd = bond;
        }

        if (smd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        ProcInputData input = new ProcInputData();
        // Per calcolo realized/unrealized si utilizza sempre clean price
        // componente accrual calcolata a parte, andranno a CE come Interessi Attivi/Passivi
        // o competenze cedolari attive/passive. Utilizzo il prezzo base unitaria
        input.setPrice(txn.getPrice() * smd.getMultiplier());
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatusPreElab());
        // trasformo prezzo di mercato in base unitaria per calcolo utili unrealized
        super.process(input, position);

        Calendar calendar = new Calendar(smd.getCurrency());
        LocalDate valuationDate = calendar.getNextBusinessDate(txn.getTradeDate().toLocalDate(), smd.getBusinessDays());
        XRBInputData bondInputData = new XRBInputData();
        bondInputData.setReferencePrice(txn.getPrice());
        bondInputData.setValuationDate(valuationDate);
        bondInputData.setCompounding(Compounding.COMPOUNDED);
        bondInputData.setDaycount(smd.getAccrualDaycount());
        bondInputData.setFrequency(smd.getFrequency());
        List<CashFlow> flows = getFlows(smd.getCashFlows());
        bondInputData.setFlows(flows);
        XRBOutputData bondOutputData = bondPricer.calculate(bondInputData);

        // Per accrual stessa gestione di quantity e notionalValue
        // Se transazione cancellata o modificata, inverto quantita, 
        // mantenendo pero il side
        double quantity = input.getQuantity();
        if (input.getStatus() == TxnStatus.TO_CANCEL
                || input.getStatus() == TxnStatus.TO_AMEND) {
            quantity = quantity * (-1.);
        }
        double accruals = bondOutputData.getAccruedInterest() * quantity * smd.getMultiplier();
        switch (input.getSide()) {
            case BUY -> {
                position.setBuyAccrual(position.getBuyAccrual() + accruals);
            }
            case SELL -> {
                position.setSellAccrual(position.getSellAccrual() + accruals);
            }
        }

        // Aggiorno accrual transazione. Sono calcolati sempre a trade + 2
        if (txn.getTxnStatusPreElab() == PENDING || txn.getTxnStatusPreElab() == RESTARTING) {
            FinancialTxnComponent accrualComponent = new FinancialTxnComponent();
            accrualComponent.setCurrency(smd.getCurrency());
            accrualComponent.setDescription("Accruals txn: " + txn.getIdFinancialTxn());
            accrualComponent.setComponentType(TxnComponentType.BOND_ACCRUAL);
            accrualComponent.setAmount(BigDecimal.valueOf(accruals));
            txn.addTxnComponent(accrualComponent);
        }
        position.setYtm(bondOutputData.getYtm());
        position.setModDuration(bondOutputData.getModifiedDuration());
        position.setDuration(bondOutputData.getModifiedDuration());
        LocalDate maturity = smd.getMaturityDate().toLocalDate();
        double timeToMaturity = smd.getDaycount().calculate(valuationDate, maturity, smd.getFrequency());
        position.setTimeToMaturity(timeToMaturity);
        position.setTheoreticalPrice(txn.getPrice() + bondOutputData.getAccruedInterest());
        // sulla posizione tengo market price normalizzato a base 100
        position.setMarketPrice(txn.getPrice());
    }

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    private List<CashFlow> getFlows(List<CashFlowItem> cashFlows) {
        List<CashFlow> flows = null;

        if (!cashFlows.isEmpty()) {
            flows = new ArrayList<>();
            for (CashFlowItem item : cashFlows) {
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
        }
        return flows;
    }
}
