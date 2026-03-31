/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

import java.util.List;
import ph.alephzero.finance.cashflows.CashFlowItem;

/**
 *
 * @author softc
 */
public class BondForwardInputData extends ForwardInputData{

    // Lista cedole sottostante
    protected List<CashFlowItem> underliyngCashFlows = null;
 
    /**
     * @return the underliyngCashFlows
     */
    public List<CashFlowItem> getUnderliyngCashFlows() {
        return underliyngCashFlows;
    }

    /**
     * @param underliyngCashFlows the underliyngCashFlows to set
     */
    public void setUnderliyngCashFlows(List<CashFlowItem> underliyngCashFlows) {
        this.underliyngCashFlows = underliyngCashFlows;
    }

}
