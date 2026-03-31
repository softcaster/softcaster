/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.loan;

import org.softcaster.commons.types.Date;

/**
 *
 * @author ep
 */
public class LoanInfo {
    
    public Date start = new Date();
    public Date end = new Date();
    // Tasso annuo
    public double rate = 0;
    // Tasso periodale
    public double nopRate = 0;
    public double amount = 0;
    // Periodi nell'anno (frequency)
    public int nop = 0;
    // Totale periodi
    public long totalNop = 0;
    public double initialInstallment = 0;

    public void initialize() {
        initialInstallment = 0;
        long years = end.days(start) / 365;
        totalNop = years * nop;
        nopRate = (rate / 100) / nop;
        initialInstallment = amount * (nopRate / (1 - (1 / Math.pow(1 + nopRate, totalNop))));
    }

    public double getPrincipal(int k) {
        return initialInstallment / Math.pow(1 + nopRate, totalNop - k);
    }
}
