/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import org.softcaster.core.data.TxnStatus;

/**
 *
 * @author ep
 */
public record FinancialTxnDto(
        Integer financialTxnId,
        // Controparte
        Integer counterpartyId,
        String counterpartyCode,
        String counterpartyDesc,
        // Posizione
        Integer positionMdId,
        String positionMdCode,
        // Anagrafica
        Integer masterDataId,
        String masterDataCode,
        String masterDataDesc,
        TxnStatus txnStatus,
        Integer refId,
        Short txnSide,
        java.sql.Date tradeDate,
        java.sql.Date settlement,
        Double quantity,
        Double price) {}
