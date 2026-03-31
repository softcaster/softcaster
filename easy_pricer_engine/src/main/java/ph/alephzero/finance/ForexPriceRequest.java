/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author ep
 */
public class ForexPriceRequest extends PriceRequest {
    private String ccy = "";

    /**
     * @return the ccy
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * @param ccy the ccy to set
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }
    
}
