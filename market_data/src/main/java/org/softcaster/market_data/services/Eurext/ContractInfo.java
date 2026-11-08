/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.market_data.services.Eurext;

public record ContractInfo(int contractId, String contract, String isin, String expirationDate) {
}
