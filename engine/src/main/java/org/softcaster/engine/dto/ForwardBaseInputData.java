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

    private double domesticRate = 0; // tasso free-risk
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

}
