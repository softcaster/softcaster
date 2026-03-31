/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.futures;

/**
 *
 * @author ep
 */
public class BtpMiniFutureOutputData {
    // CTD
    private String ctdISIN = "";
    // Prezzo CTD
    private double ctdPrice = 0.;
    // Prezzo teorico del Future
    private double theoreticalPrice = 0.;
    private double positionValue = 0.;
    // Basis
    private double basis = 0.;
    // Implied Repo Rate
    private double irr = 0.;

    /**
     * @return the ctdISIN
     */
    public String getCtdISIN() {
        return ctdISIN;
    }

    /**
     * @param ctdISIN the ctdISIN to set
     */
    public void setCtdISIN(String ctdISIN) {
        this.ctdISIN = ctdISIN;
    }

    /**
     * @return the ctdPrice
     */
    public double getCtdPrice() {
        return ctdPrice;
    }

    /**
     * @param ctdPrice the ctdPrice to set
     */
    public void setCtdPrice(double ctdPrice) {
        this.ctdPrice = ctdPrice;
    }

    /**
     * @return the theoreticalPrice
     */
    public double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    /**
     * @param theoreticalPrice the theoreticalPrice to set
     */
    public void setTheoreticalPrice(double theoreticalPrice) {
        this.theoreticalPrice = theoreticalPrice;
    }

    /**
     * @return the basis
     */
    public double getBasis() {
        return basis;
    }

    /**
     * @param basis the basis to set
     */
    public void setBasis(double basis) {
        this.basis = basis;
    }

    /**
     * @return the irr
     */
    public double getIrr() {
        return irr;
    }

    /**
     * @param irr the irr to set
     */
    public void setIrr(double irr) {
        this.irr = irr;
    }

    /**
     * @return the positionValue
     */
    public double getPositionValue() {
        return positionValue;
    }

    /**
     * @param positionValue the positionValue to set
     */
    public void setPositionValue(double positionValue) {
        this.positionValue = positionValue;
    }
    
}
