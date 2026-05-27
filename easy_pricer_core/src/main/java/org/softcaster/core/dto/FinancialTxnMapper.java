/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
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

    public FinancialTxn fromDto(FinancialTxn FinancialTxnDto) {
        
        FinancialTxn financialTxn;
        
        // Caso nuova transazione
        if(FinancialTxnDto.getIdFinancialTxn() == 0) {
            financialTxn = new FinancialTxn();
        }
        // Caso transazione gia`esistente, sono in modifica
        else {
            financialTxn = financialTxnDAO.findByIdFinancialTxn(FinancialTxnDto.getIdFinancialTxn());
        }
        
        // Aggiorno counterparty, position, segno ...
        
        return financialTxn;
    }

}
