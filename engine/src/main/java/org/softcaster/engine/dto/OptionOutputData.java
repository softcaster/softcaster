/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class OptionOutputData extends MarketOutputData{
    
    private double delta = 0.;
    private double gamma = 0.;
    private double vega = 0.;
    private double theta = 0.;
    private double rhoD = 0.;
    private double rhoF = 0.;

    /**
     * @return the delta
     */
    public double getDelta() {
        return delta;
    }

    /**
     * @param delta the delta to set
     */
    public void setDelta(double delta) {
        this.delta = delta;
    }

    /**
     * @return the gamma
     */
    public double getGamma() {
        return gamma;
    }

    /**
     * @param gamma the gamma to set
     */
    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    /**
     * @return the vega
     */
    public double getVega() {
        return vega;
    }

    /**
     * @param vega the vega to set
     */
    public void setVega(double vega) {
        this.vega = vega;
    }

    /**
     * @return the theta
     */
    public double getTheta() {
        return theta;
    }

    /**
     * @param theta the theta to set
     */
    public void setTheta(double theta) {
        this.theta = theta;
    }

    /**
     * @return the rhoD
     */
    public double getRhoD() {
        return rhoD;
    }

    /**
     * @param rhoD the rhoD to set
     */
    public void setRhoD(double rhoD) {
        this.rhoD = rhoD;
    }

    /**
     * @return the rhoF
     */
    public double getRhoF() {
        return rhoF;
    }

    /**
     * @param rhoF the rhoF to set
     */
    public void setRhoF(double rhoF) {
        this.rhoF = rhoF;
    }
}
