/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author softc
 */
public class BondFwdPriceRequest extends PriceRequest {

    private java.sql.Date maturityDate = null;
    private double repoRate = 0.;

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
     * @return the repoRate
     */
    public double getRepoRate() {
        return repoRate;
    }

    /**
     * @param repoRate the repoRate to set
     */
    public void setRepoRate(double repoRate) {
        this.repoRate = repoRate;
    }

}
