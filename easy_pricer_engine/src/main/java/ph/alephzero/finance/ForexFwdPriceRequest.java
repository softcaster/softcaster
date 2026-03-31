/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author ep
 */
public class ForexFwdPriceRequest extends ForexPriceRequest {

    private java.sql.Date maturityDate = null;
    private double bcyRate = 0.;
    private double ccyRate = 0.;

    /**
     * @return the maturityDate
     */
    public java.sql.Date getMaturityDate() {
        return maturityDate;
    }

    /**
     * @param maturityDate the maturityDate to set
     */
    public void setMaturityDate(java.sql.Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    /**
     * @return the bcyRate
     */
    public double getBcyRate() {
        return bcyRate;
    }

    /**
     * @param bcyRate the bcyRate to set
     */
    public void setBcyRate(double bcyRate) {
        this.bcyRate = bcyRate;
    }

    /**
     * @return the ccyRate
     */
    public double getCcyRate() {
        return ccyRate;
    }

    /**
     * @param ccyRate the ccyRate to set
     */
    public void setCcyRate(double ccyRate) {
        this.ccyRate = ccyRate;
    }

}
