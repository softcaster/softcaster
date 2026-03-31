/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.futures;

import ph.alephzero.finance.SettlementType;
import ph.alephzero.finance.products.forward.ForwardInputData;

/**
 *
 * @author softc
 */
public class FutureInputData extends ForwardInputData{
    
    // Prezzo mercato del Future
    private double futurePrice = 0;
    
    // Modalita consegna
    private SettlementType settlementType;

    /**
     * @return the futurePrice
     */
    public double getFuturePrice() {
        return futurePrice;
    }

    /**
     * @param futurePrice the futurePrice to set
     */
    public void setFuturePrice(double futurePrice) {
        this.futurePrice = futurePrice;
    }

    /**
     * @return the settlementType
     */
    public SettlementType getSettlementType() {
        return settlementType;
    }

    /**
     * @param settlementType the settlementType to set
     */
    public void setSettlementType(SettlementType settlementType) {
        this.settlementType = settlementType;
    }


}
