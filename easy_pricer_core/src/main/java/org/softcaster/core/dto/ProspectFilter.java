/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

/**
 *
 * @author softc
 */
public class ProspectFilter {
    private Integer positionId;
    private Integer counterpartyId;
    private Integer assetClassId;

    /**
     * @return the positionId
     */
    public Integer getPositionId() {
        return positionId;
    }

    /**
     * @param positionId the positionId to set
     */
    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    /**
     * @return the counterpartyId
     */
    public Integer getCounterpartyId() {
        return counterpartyId;
    }

    /**
     * @param counterpartyId the counterpartyId to set
     */
    public void setCounterpartyId(Integer counterpartyId) {
        this.counterpartyId = counterpartyId;
    }

    /**
     * @return the assetClassId
     */
    public Integer getAssetClassId() {
        return assetClassId;
    }

    /**
     * @param assetClassId the assetClassId to set
     */
    public void setAssetClassId(Integer assetClassId) {
        this.assetClassId = assetClassId;
    }    
}
