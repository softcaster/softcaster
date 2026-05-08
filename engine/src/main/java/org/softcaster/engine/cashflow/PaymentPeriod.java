/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;

public record PaymentPeriod(
        LocalDate accrualStart, // Inizio maturazione interessi (teorico)
        LocalDate accrualEnd, // Fine maturazione interessi (teorico)
        LocalDate paymentDate, // Data effettiva di pagamento (post-festivi)
        double yearFraction // Quota dell'anno calcolata (es. ACT/360)
        ) {

}
