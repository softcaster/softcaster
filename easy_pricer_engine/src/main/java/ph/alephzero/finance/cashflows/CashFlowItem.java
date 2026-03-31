/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.cashflows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ep
 */
public class CashFlowItem {

    private Date start;
    private Date end;
    private double interest;
    private double amount;
    private double discountFactors;

    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }

    /**
     * @return the interest
     */
    public double getInterest() {
        return interest;
    }

    /**
     * @param interest the interest to set
     */
    public void setInterest(double interest) {
        this.interest = interest;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * @return the discountFactors
     */
    public double getDiscountFactors() {
        return discountFactors;
    }

    /**
     * @param discountFactors the discountFactors to set
     */
    public void setDiscountFactors(double discountFactors) {
        this.discountFactors = discountFactors;
    }

    @Override
    public String toString() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String result = "Start Period: " + formatter.format(start) + " : " + "End Period: " + formatter.format(end) + " : " + "Interest: " + interest + " : " + "Amount: " + amount;
        return result;
    }

    public String toCsv() {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String result = formatter.format(start) + "," + formatter.format(end) + "," + interest + "," + amount;
        return result;
    }
}
