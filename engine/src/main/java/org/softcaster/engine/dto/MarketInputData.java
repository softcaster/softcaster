/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public abstract class MarketInputData implements IMarketInputData{

    private double spotPrice;      // Prezzo attuale (Cambio FX, Stock Price o Bond Clean Price)
    private LocalDate valuationDate; // Data "As-of"
    private DaycountBasis daycount;
    private Frequency Frequency; // Necessaria se daycount ACT_ACT_ICMA
    private Compounding compounding;

    /**
     * @return the spotPrice
     */
    public double getSpotPrice() {
        return spotPrice;
    }

    /**
     * @param spotPrice the spotPrice to set
     */
    public void setSpotPrice(double spotPrice) {
        this.spotPrice = spotPrice;
    }

    /**
     * @return the valuationDate
     */
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    /**
     * @param valuationDate the valuationDate to set
     */
    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }

    /**
     * @return the daycount
     */
    public DaycountBasis getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(DaycountBasis daycount) {
        this.daycount = daycount;
    }

    /**
     * @return the compounding
     */
    public Compounding getCompounding() {
        return compounding;
    }

    /**
     * @param compounding the compounding to set
     */
    public void setCompounding(Compounding compounding) {
        this.compounding = compounding;
    }

    /**
     * @return the Frequency
     */
    public Frequency getFrequency() {
        return Frequency;
    }

    /**
     * @param Frequency the Frequency to set
     */
    public void setFrequency(Frequency Frequency) {
        this.Frequency = Frequency;
    }
    
}
