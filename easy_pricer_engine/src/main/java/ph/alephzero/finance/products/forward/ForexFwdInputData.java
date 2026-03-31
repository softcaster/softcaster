/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author ep
 */
public class ForexFwdInputData extends ForwardInputData {
    // Free risk rate da settlement a maturity (repo rate)
    protected double rateCcy = 0;
    protected DayCountBasis daycountCcy = DayCountBasis.ACT_ACT;

    /**
     * @return the rateCcy
     */
    public double getRateCcy() {
        return rateCcy;
    }

    /**
     * @param rateCcy the rateCcy to set
     */
    public void setRateCcy(double rateCcy) {
        this.rateCcy = rateCcy;
    }

    /**
     * @return the daycountCcy
     */
    public DayCountBasis getDaycountCcy() {
        return daycountCcy;
    }

    /**
     * @param daycountCcy the daycountCcy to set
     */
    public void setDaycountCcy(DayCountBasis daycountCcy) {
        this.daycountCcy = daycountCcy;
    }


}
