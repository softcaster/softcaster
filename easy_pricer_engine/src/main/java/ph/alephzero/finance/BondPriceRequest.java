/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author ep
 */
public class BondPriceRequest extends PriceRequest {

    private String yieldCurve = null;
    private boolean fullCalc = false;

    /**
     * @return the yieldCurve
     */
    public String getYieldCurve() {
        return yieldCurve;
    }

    /**
     * @param yieldCurve the yieldCurve to set
     */
    public void setYieldCurve(String yieldCurve) {
        this.yieldCurve = yieldCurve;
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
