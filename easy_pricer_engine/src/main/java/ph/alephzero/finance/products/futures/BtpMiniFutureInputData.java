/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.futures;

import java.util.List;
import ph.alephzero.finance.cashflows.CashFlowItem;

/**
 *
 * @author softc
 */
public class BtpMiniFutureInputData extends FutureInputData {
    //
    // Dati anagrafici
    //
    private double initialMagin = 0;
    private double contractValue = 0;
    private double tick = 0.01;
    private int nrOfContracts = 1;
    private double maintenanceMargin = 0;
    private double conversionFactor = 1;
    
    //
    // Dati di mercato
    //
    // Prezzo di carico del Bond Future
    private double invoicePrice = 0;
    // Prezzo del sottostante (CTD)
    private double underliyngPrice = 0;
    // Lista cedole sottostante (teoricamente il CTD)
    private List<CashFlowItem> underliyngCashFlows = null;
    

    /**
     * @return the initialMagin
     */
    public double getInitialMagin() {
        return initialMagin;
    }

    /**
     * @param initialMagin the initialMagin to set
     */
    public void setInitialMagin(double initialMagin) {
        this.initialMagin = initialMagin;
    }

    /**
     * @return the contractValue
     */
    public double getContractValue() {
        return contractValue;
    }

    /**
     * @param contractValue the contractValue to set
     */
    public void setContractValue(double contractValue) {
        this.contractValue = contractValue;
    }

    /**
     * @return the tick
     */
    public double getTick() {
        return tick;
    }

    /**
     * @param tick the tick to set
     */
    public void setTick(double tick) {
        this.tick = tick;
    }

    public double getTickValue() {
        return tick * contractValue * nrOfContracts;
    }

    /**
     * @return the nrOfContracts
     */
    public int getNrOfContracts() {
        return nrOfContracts;
    }

    /**
     * @param nrOfContracts the nrOfContracts to set
     */
    public void setNrOfContracts(int nrOfContracts) {
        this.nrOfContracts = nrOfContracts;
    }   

    /**
     * @return the maintenanceMargin
     */
    public double getMaintenanceMargin() {
        return maintenanceMargin;
    }

    /**
     * @param maintenanceMargin the maintenanceMargin to set
     */
    public void setMaintenanceMargin(double maintenanceMargin) {
        this.maintenanceMargin = maintenanceMargin;
    }

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

    /**
     * @return the conversionFactor
     */
    public double getConversionFactor() {
        return conversionFactor;
    }

    /**
     * @param conversionFactor the conversionFactor to set
     */
    public void setConversionFactor(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    /**
     * @return the underliyngPrice
     */
    public double getUnderliyngPrice() {
        return underliyngPrice;
    }

    /**
     * @param underliyngPrice the underliyngPrice to set
     */
    public void setUnderliyngPrice(double underliyngPrice) {
        this.underliyngPrice = underliyngPrice;
    }

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
