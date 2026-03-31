/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

/**
 *
 * @author ep
 */
public class ForexFwdOutputData {

    // Prezzo teorico del Forward
    private double theoreticalPrice = 0.;
    
    // forward points
    private double forwardPoints = 0;
    
    //Poiché i Future hanno flussi di cassa intermedi e i Forward no, 
    //esiste un differenziale di valore chiamato convexity adjustment. 
    //Se i tassi di interesse sono molto volatili, la differenza tra il prezzo Future 
    //e quello Forward aumenta perché il valore temporale del denaro incassato (o pagato) giornalmente diventa più incerto.
    private double convexityAdjustment = 0;

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
     * @return the forwardPoints
     */
    public double getForwardPoints() {
        return forwardPoints;
    }

    /**
     * @param forwardPoints the forwardPoints to set
     */
    public void setForwardPoints(double forwardPoints) {
        this.forwardPoints = forwardPoints;
    }

    /**
     * @return the convexityAdjustment
     */
    public double getConvexityAdjustment() {
        return convexityAdjustment;
    }

    /**
     * @param convexityAdjustment the convexityAdjustment to set
     */
    public void setConvexityAdjustment(double convexityAdjustment) {
        this.convexityAdjustment = convexityAdjustment;
    }

}
