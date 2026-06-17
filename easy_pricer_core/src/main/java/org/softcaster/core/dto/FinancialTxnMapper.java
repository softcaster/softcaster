/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import org.softcaster.core.data.Counterparty;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class FinancialTxnMapper {

    @Autowired
    FinancialTxnDAO financialTxnDAO;

    @Autowired
    MasterDataDAO masterDataDAO;
    @Autowired
    CounterpartyDAO counterpartyDAO;
    @Autowired
    PositionMasterDataDAO positionMasterDataDAO;

    public FinancialTxnDto toDto(FinancialTxn entity) {
        if (entity == null) {
            return null;
        }

        return new FinancialTxnDto(
                entity.getIdFinancialTxn(),
                entity.getDescription(),
                entity.getCounterparty() != null ? entity.getCounterparty().getIdCounterparty() : null,
                entity.getCounterparty() != null ? entity.getCounterparty().getCode() : null,
                entity.getCounterparty() != null ? entity.getCounterparty().getDescription() : null,
                entity.getPositionMd() != null ? entity.getPositionMd().getIdPosition() : null,
                entity.getPositionMd() != null ? entity.getPositionMd().getCode() : null,
                entity.getMasterData() != null ? entity.getMasterData().getIdMasterData() : null,
                entity.getMasterData() != null ? entity.getMasterData().getCode() : null,
                entity.getMasterData() != null ? entity.getMasterData().getDescription() : null,
                entity.getTxnStatus() != null ? entity.getTxnStatus().getId() : null,
                entity.getTxnStatus() != null ? entity.getTxnStatus().getCode() : null,
                entity.getTxnStatus() != null ? entity.getTxnStatus().getDescription() : null,
                entity.getRefId(),
                entity.getTxnSide() != null ? (short)(entity.getTxnSide().getId()) : null,
                entity.getTradeDate(),
                entity.getSettlement(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }

    public FinancialTxn fromDto(FinancialTxnDto financialTxnDto) {

        FinancialTxn financialTxn;
        MasterData mds = null;
        PositionMasterData pmd = null;
        Counterparty ctp = null;

        // Sia in caso di nuova transazione che di modifica creo una nuova
        // transazione, non chiamo nessuna findByIdFinancialTxn altrimenti
        // Hibernate cacha la nuova transazione ed ogni modifica si riflette
        // anche sulla vecchia salvata su db
        financialTxn = new FinancialTxn();
        financialTxn.setIdFinancialTxn(0);
        // Setto oggetti complessi
        mds = masterDataDAO.findByIdMasterData(financialTxnDto.masterDataId());
        financialTxn.setMasterData(mds);
        pmd = positionMasterDataDAO.findByIdPosition(financialTxnDto.positionMdId());
        financialTxn.setPositionMd(pmd);
        ctp = counterpartyDAO.findByIdCounterparty(financialTxnDto.counterpartyId());
        financialTxn.setCounterparty(ctp);
        financialTxn.setDescription(financialTxnDto.description());
        financialTxn.setPrice(financialTxnDto.price());
        financialTxn.setQuantity(financialTxnDto.quantity());
        financialTxn.setTradeDate(financialTxnDto.tradeDate());
        financialTxn.setValueDate(financialTxnDto.tradeDate());
        financialTxn.setSettlement(financialTxnDto.tradeDate());
        financialTxn.setTxnSide(TxnSide.fromId(financialTxnDto.txnSide()));
        TxnStatus status = TxnStatus.fromId(financialTxnDto.txnStatusId());
        financialTxn.setTxnStatus(status);
        // Aggiorno counterparty, position, segno ...
        return financialTxn;
    }

}
