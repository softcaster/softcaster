/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 *
 * @author softc
 */
public class ForwardBaseInputData extends MarketInputData {
    // 1) in generale tasso free-risk
    // 2) In caso di fx, rappresenta il tasso free-risk della ccy
    private double domesticRate = 0; 

    // 1) Nel Forex domesticRate è il tasso della ccy e
    // foreignRate il tasso della bcy.
    // es nel caso EUR/USD il tasso del EUR (bcy) e' il foreignRate
    // e il tasso dell USD (ccy) il domestic rate
    // 2) Nel caso di equity e' il dividend yield
    private double foreignRate;  

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
}
