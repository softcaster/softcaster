/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import java.io.Serializable;

/**
 *
 * @author ep
 */
public class CalcInputData implements Serializable {

    private String isin = "";
    private double cleanPrice = 0.0;
    private java.sql.Date settlementDate = null;

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
     * @return the cleanPrice
     */
    public double getCleanPrice() {
        return cleanPrice;
    }

    /**
     * @param cleanPrice the cleanPrice to set
     */
    public void setCleanPrice(double cleanPrice) {
        this.cleanPrice = cleanPrice;
    }

    /**
     * @return the settlementDate
     */
    public java.sql.Date getSettlementDate() {
        return settlementDate;
    }

    /**
     * @param settlementDate the settlementDate to set
     */
    public void setSettlementDate(java.sql.Date settlementDate) {
        this.settlementDate = settlementDate;
    }
}
