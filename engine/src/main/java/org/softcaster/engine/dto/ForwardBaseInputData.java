/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import org.softcaster.engine.curve.YieldCurve;

/**
 *
 * @author softc
 */
public class ForwardBaseInputData extends MarketInputData {
    
    // Prezzo sottostante
    private double underlyingReferencePrice;
    
    // 1) in generale tasso free-risk
    // 2) In caso di fx, rappresenta il tasso free-risk della ccy
    private double domesticRate = 0; 
    private YieldCurve domesticRateCurve; 

    // 1) Nel Forex domesticRate è il tasso della ccy e
    // foreignRate il tasso della bcy.
    // es nel caso EUR/USD il tasso del EUR (bcy) e' il foreignRate
    // e il tasso dell USD (ccy) il domestic rate
    // 2) Nel caso di equity e' il dividend yield
    private double foreignRate;  
    private YieldCurve foreignRateCurve; 
    
    private LocalDate maturityDate;

    /**
     * @return the domesticRate
     */
    public double getDomesticRate() {
        return domesticRate;
    }

    /**
     * @param domesticRate the domesticRate to set
     */
    public void setDomesticRate(double domesticRate) {
        this.domesticRate = domesticRate;
    }

    /**
     * @return the maturityDate
     */
    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    /**
     * @param maturityDate the maturityDate to set
     */
    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    /**
     * @return the foreignRate
     */
    public double getForeignRate() {
        return foreignRate;
    }

    /**
     * @param foreignRate the foreignRate to set
     */
    public void setForeignRate(double foreignRate) {
        this.foreignRate = foreignRate;
    }

    /**
     * @return the domesticRateCurve
     */
    public YieldCurve getDomesticRateCurve() {
        return domesticRateCurve;
    }

    /**
     * @param domesticRateCurve the domesticRateCurve to set
     */
    public void setDomesticRateCurve(YieldCurve domesticRateCurve) {
        this.domesticRateCurve = domesticRateCurve;
    }

    /**
     * @return the foreignRateCurve
     */
    public YieldCurve getForeignRateCurve() {
        return foreignRateCurve;
    }

    /**
     * @param foreignRateCurve the foreignRateCurve to set
     */
    public void setForeignRateCurve(YieldCurve foreignRateCurve) {
        this.foreignRateCurve = foreignRateCurve;
    }

    /**
     * @return the underlyingReferencePrice
     */
    public double getUnderlyingReferencePrice() {
        return underlyingReferencePrice;
    }

    /**
     * @param underlyingReferencePrice the underlyingReferencePrice to set
     */
    public void setUnderlyingReferencePrice(double underlyingReferencePrice) {
        this.underlyingReferencePrice = underlyingReferencePrice;
    }
}
