/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import java.util.Date;
import java.util.List;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlowItem;

/**
 *
 * @author ep
 */
public class BondCalcInputData {

    private Date settlement; // valuation date
    private double currentPrice;
    private Date issue;
    private Date maturity;
    private Date firstCoupon;
    private Date lastCoupon;
    private int frequency;
    private double couponRate;
    private double issuePrice;
    private double redemptionPrice;
    private DayCountBasis basis;
    private List<CashFlowItem> cashFlows = null;
    private boolean fullCalc = false;

    /**
     * @return the settlement
     */
    public Date getSettlement() {
        return settlement;
    }

    /**
     * @param settlement the settlement to set
     */
    public void setSettlement(Date settlement) {
        this.settlement = settlement;
    }

    /**
     * @return the currentPrice
     */
    public double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * @param currentPrice the currentPrice to set
     */
    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    /**
     * @return the issue
     */
    public Date getIssue() {
        return issue;
    }

    /**
     * @param issue the issue to set
     */
    public void setIssue(Date issue) {
        this.issue = issue;
    }

    /**
     * @return the maturity
     */
    public Date getMaturity() {
        return maturity;
    }

    /**
     * @param maturity the maturity to set
     */
    public void setMaturity(Date maturity) {
        this.maturity = maturity;
    }

    /**
     * @return the firstCoupon
     */
    public Date getFirstCoupon() {
        return firstCoupon;
    }

    /**
     * @param firstCoupon the firstCoupon to set
     */
    public void setFirstCoupon(Date firstCoupon) {
        this.firstCoupon = firstCoupon;
    }

    /**
     * @return the lastCoupon
     */
    public Date getLastCoupon() {
        return lastCoupon;
    }

    /**
     * @param lastCoupon the lastCoupon to set
     */
    public void setLastCoupon(Date lastCoupon) {
        this.lastCoupon = lastCoupon;
    }

    /**
     * @return the frequency
     */
    public int getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the couponRate
     */
    public double getCouponRate() {
        return couponRate;
    }

    /**
     * @param couponRate the couponRate to set
     */
    public void setCouponRate(double couponRate) {
        this.couponRate = couponRate;
    }

    /**
     * @return the issuePrice
     */
    public double getIssuePrice() {
        return issuePrice;
    }

    /**
     * @param issuePrice the issuePrice to set
     */
    public void setIssuePrice(double issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * @return the redemptionPrice
     */
    public double getRedemptionPrice() {
        return redemptionPrice;
    }

    /**
     * @param redemptionPrice the redemptionPrice to set
     */
    public void setRedemptionPrice(double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    /**
     * @return the basis
     */
    public DayCountBasis getBasis() {
        return basis;
    }

    /**
     * @param basis the basis to set
     */
    public void setBasis(DayCountBasis basis) {
        this.basis = basis;
    }

    /**
     * @return the cashFlows
     */
    public List<CashFlowItem> getCashFlows() {
        return cashFlows;
    }

    /**
     * @param cashFlows the cashFlows to set
     */
    public void setCashFlows(List<CashFlowItem> cashFlows) {
        this.cashFlows = cashFlows;
    }

    /**
     * @return the fullCalc
     */
    public boolean isFullCalc() {
        return fullCalc;
    }

    /**
     * @param fullCalc the fullCalc to set
     */
    public void setFullCalc(boolean fullCalc) {
        this.fullCalc = fullCalc;
    }
    
    
}
