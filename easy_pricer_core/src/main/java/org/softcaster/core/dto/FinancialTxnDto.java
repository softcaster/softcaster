/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

/**
 *
 * @author ep
 */
public record FinancialTxnDto(
        Integer financialTxnId,
        String description,
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
        Integer txnStatusId,
        String txnStatusCode,
        String txnStatusDescription,
        Integer refId,
        Short txnSide,
        java.sql.Date tradeDate,
        java.sql.Date settlement,
        Double quantity,
        Double price,
        Double fxRate,
        Integer txnAcctPhase) {

}
