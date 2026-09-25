/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.DeliveryPeriodType;
import org.softcaster.engine.enums.LoadType;

@Entity
@Table(name = "power_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "PowerFutureMasterData.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass"),
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("deliveryProfile"),}
    )
})
public class PowerFutureMasterData extends CmdFutureMasterData {

    @Convert(converter = LoadTypeConverter.class)
    private LoadType loadType;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "masterData"
    )
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
    protected CommodityDeliveryProfile getDeliveryProfile() {
        return deliveryProfile;
    }

    /**
     * @param deliveryProfile the deliveryProfile to set
     */
    protected void setDeliveryProfile(CommodityDeliveryProfile deliveryProfile) {
        // Evita di fare inutilmente il lavoro se è già lo stesso oggetto
        if (this.deliveryProfile == deliveryProfile) {
            return;
        }

        this.deliveryProfile = deliveryProfile;

        if (deliveryProfile != null
                && deliveryProfile.getMasterData() != this) {
            deliveryProfile.setMasterData(this);
        }
    }

    public DeliveryPeriodType getDeliveryPeriodType() {
        if (deliveryProfile == null) {
            deliveryProfile = new CommodityDeliveryProfile();
        }
        return deliveryProfile != null ? deliveryProfile.getDeliveryPeriodType() : null;
    }

    /**
     * @param deliveryPeriodType the deliveryPeriodType to set
     */
    public void setDeliveryPeriodType(DeliveryPeriodType deliveryPeriodType) {
        getOrCreateDeliveryProfile().setDeliveryPeriodType(deliveryPeriodType);
    }

    /**
     * @return the deliveryStart
     */
    public java.sql.Date getDeliveryStart() {
        // Nessuna creazione nelle get
        return deliveryProfile != null ? deliveryProfile.getDeliveryStart() : null;
    }

    /**
     * @param deliveryStart the deliveryStart to set
     */
    public void setDeliveryStart(java.sql.Date deliveryStart) {
        getOrCreateDeliveryProfile().setDeliveryStart(deliveryStart);
    }

    /**
     * @return the deliveryEnd
     */
    public java.sql.Date getDeliveryEnd() {
        return deliveryProfile != null ? deliveryProfile.getDeliveryEnd() : null;
    }

    /**
     * @param deliveryEnd the deliveryEnd to set
     */
    public void setDeliveryEnd(java.sql.Date deliveryEnd) {
        getOrCreateDeliveryProfile().setDeliveryEnd(deliveryEnd);
    }

    /**
     * @return the totalDeliveryHours
     */
    public Integer getTotalDeliveryHours() {
        return deliveryProfile != null ? deliveryProfile.getTotalDeliveryHours() : null;
    }

    /**
     * @param totalDeliveryHours the totalDeliveryHours to set
     */
    public void setTotalDeliveryHours(Integer totalDeliveryHours) {
        getOrCreateDeliveryProfile().setTotalDeliveryHours(totalDeliveryHours);
    }

    /**
     * @return the market
     */
    public String getMarket() {
        return deliveryProfile != null ? deliveryProfile.getMarket() : null;
    }

    /**
     * @param market the market to set
     */
    public void setMarket(String market) {
        getOrCreateDeliveryProfile().setMarket(market);
    }

    /**
     * @return the notionalMw
     */
    public Double getNotionalMw() {
        return deliveryProfile != null ? deliveryProfile.getNotionalMw() : null;
    }

    /**
     * @param notionalMw the notionalMw to set
     */
    public void setNotionalMw(Double notionalMw) {
        getOrCreateDeliveryProfile().setNotionalMw(notionalMw);
    }

    /**
     * @return the notionalMwh
     */
    public Double getNotionalMwh() {
        return deliveryProfile != null ? deliveryProfile.getNotionalMwh() : null;
    }

    /**
     * @param notionalMwh the notionalMwh to set
     */
    public void setNotionalMwh(Double notionalMwh) {
        getOrCreateDeliveryProfile().setNotionalMwh(notionalMwh);
    }

    private CommodityDeliveryProfile getOrCreateDeliveryProfile() {
        if (deliveryProfile == null) {
            setDeliveryProfile(new CommodityDeliveryProfile());
        }

        return deliveryProfile;
    }
}
