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

    private String code;      // ISIN
    private double referencePrice;      // Prezzo attuale (Cambio FX, Stock Price o Bond Clean Price)
    private LocalDate referenceDate; // Data "As-of"
    private LocalDate valuationDate; 
    private DaycountBasis daycount;
    private Frequency Frequency; // Necessaria se daycount ACT_ACT_ICMA
    private Compounding compounding;

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

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the referenceDate
     */
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    /**
     * @param referenceDate the referenceDate to set
     */
    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    /**
     * @return the referencePrice
     */
    public double getReferencePrice() {
        return referencePrice;
    }

    /**
     * @param referencePrice the referencePrice to set
     */
    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }
}
