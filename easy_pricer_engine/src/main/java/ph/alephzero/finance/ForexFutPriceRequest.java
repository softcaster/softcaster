/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance;

/**
 *
 * @author ep
 */
public class ForexFutPriceRequest extends ForexFwdPriceRequest {

    // Ultimo prezzo future
    private double invoicePrice = 0.;
    
    /**
     * @return the invoicePrice
     */
    public double getInvoicePrice() {
        return invoicePrice;
    }

    /**
     * @param invoicePrice the invoicePrice to set
     */
    public void setInvoicePrice(double invoicePrice) {
        this.invoicePrice = invoicePrice;
    }

}
