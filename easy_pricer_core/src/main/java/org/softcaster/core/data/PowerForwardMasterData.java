/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.LoadType;

//@Entity @Table(name = "power_forward_master_data")
public class PowerForwardMasterData extends CmdForwardMasterData {
    @Convert(converter = LoadTypeConverter.class)
    private LoadType loadType;
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_master_data")
    private CommodityDeliveryProfile deliveryProfile;

    /**
     * @return the loadType
     */
    public LoadType getLoadType() {
        return loadType;
    }

    /**
     * @param loadType the loadType to set
     */
    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }

    /**
     * @return the deliveryProfile
     */
    public CommodityDeliveryProfile getDeliveryProfile() {
        return deliveryProfile;
    }

    /**
     * @param deliveryProfile the deliveryProfile to set
     */
    public void setDeliveryProfile(CommodityDeliveryProfile deliveryProfile) {
        this.deliveryProfile = deliveryProfile;
    }
    
}
