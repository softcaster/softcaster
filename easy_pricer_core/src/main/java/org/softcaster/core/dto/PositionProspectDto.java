/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

/**
 *
 * @author softc
 */
public record PositionProspectDto(
        String positionCode,
        String assetCode,
        String assetDescription,
        String counterpartyCode,
        Double totalQuantity,
        Double averagePrice,
        Double marketPrice,
        Double marketValue,
        Double realizedPnL,
        Double unrealizedPnL
        ) {
}
