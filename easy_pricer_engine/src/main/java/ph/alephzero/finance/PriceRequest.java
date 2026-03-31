package ph.alephzero.finance;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.Serializable;

/**
 *
 * @author softc
 */
public class PriceRequest implements Serializable {
    private String isin = "";
    private java.sql.Date referenceDate = null;
    private double referencePrice = 0.;

    /**
     * @return the isin
     */
    public String getIsin() {
        return isin;
    }

    /**
     * @param isin the isin to set
     */
    public void setIsin(String isin) {
        this.isin = isin;
    }

    /**
     * @return the referenceDate
     */
    public java.sql.Date getReferenceDate() {
        return referenceDate;
    }

    /**
     * @param referenceDate the referenceDate to set
     */
    public void setReferenceDate(java.sql.Date referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * @return the referencePrice
     */
    public double getReferencePrice() {
        return referencePrice;
    }

    /**
     * @param referencePrice the referencePrice to set
     */
    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }
}