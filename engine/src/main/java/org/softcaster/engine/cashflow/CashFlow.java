/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;

public record CashFlow(
        LocalDate accrualStart, // Inizio maturazione
        LocalDate accrualEnd, // Fine maturazione (teorica)
        LocalDate paymentDate, // Data effettiva di incasso
        double principal,
        double interest,
        double outstandingBalance,
        double yearFraction // Utile salvarlo per ricalcoli veloci
        ) {

    public double getTotalAmount() {
        return principal + interest;
    }
}
