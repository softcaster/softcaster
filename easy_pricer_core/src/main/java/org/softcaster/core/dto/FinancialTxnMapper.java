/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import java.util.Objects;
import org.softcaster.core.data.Counterparty;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
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
                entity.getTxnStatus() != null ? entity.getTxnStatus() : null,
                entity.getRefId(),
                entity.getTxnSide(),
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

        // Caso nuova transazione
        if (financialTxnDto.financialTxnId() == 0) {
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
            financialTxn.setSettlement(financialTxnDto.tradeDate());
            financialTxn.setTxnSide(financialTxnDto.txnSide());
        } // Caso transazione gia`esistente, sono in modifica
        else {
            financialTxn = financialTxnDAO.findByIdFinancialTxn(financialTxnDto.financialTxnId());
            if (!Objects.equals(financialTxn.getMasterData().getIdMasterData(), financialTxnDto.masterDataId())) {
                mds = masterDataDAO.findByIdMasterData(financialTxnDto.masterDataId());
                financialTxn.setMasterData(mds);
            }
            if (!Objects.equals(financialTxn.getPositionMd().getIdPosition(), financialTxnDto.positionMdId())) {
                pmd = positionMasterDataDAO.findByIdPosition(financialTxnDto.positionMdId());
                financialTxn.setPositionMd(pmd);
            }
            if (!Objects.equals(financialTxn.getCounterparty().getIdCounterparty(), financialTxnDto.counterpartyId())) {
                ctp = counterpartyDAO.findByIdCounterparty(financialTxnDto.counterpartyId());
                financialTxn.setCounterparty(ctp);
            }
            if (!financialTxn.getDescription().equals(financialTxnDto.description())) {
                financialTxn.setDescription(financialTxnDto.description());
            }
            if (!(Objects.equals(financialTxn.getPrice(), financialTxnDto.price()))) {
                financialTxn.setPrice(financialTxnDto.price());
            }
            if (!(Objects.equals(financialTxn.getQuantity(), financialTxnDto.quantity()))) {
                financialTxn.setQuantity(financialTxnDto.quantity());
            }
            if (!(Objects.equals(financialTxn.getTradeDate(), financialTxnDto.tradeDate()))) {
                financialTxn.setTradeDate(financialTxnDto.tradeDate());
                financialTxn.setSettlement(financialTxnDto.tradeDate());
            }
            if (!(Objects.equals(financialTxn.getTxnSide(), financialTxnDto.txnSide()))) {
                financialTxn.setTxnSide(financialTxnDto.txnSide());
            }
        }

        // Aggiorno counterparty, position, segno ...
        return financialTxn;
    }

}
