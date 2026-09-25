/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.DeliveryPeriodTypeConverter;
import org.softcaster.engine.enums.DeliveryPeriodType;

@Entity
@Table(name = "commodity_delivery_profile")
public class CommodityDeliveryProfile {

    @Id
    @Column(name = "id_master_data")
    private Integer idMasterData;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // dice esplicitamente a Hibernate: "l'id di CommodityDeliveryProfile deve coincidere con l'id di PowerFutureMasterData"
    @JoinColumn(name = "id_master_data")
    private PowerFutureMasterData masterData;

    @Convert(converter = DeliveryPeriodTypeConverter.class)
    private DeliveryPeriodType deliveryPeriodType;

    private java.sql.Date deliveryStart, deliveryEnd;

    private Integer totalDeliveryHours;

    private String market;

    @JdbcTypeCode(Types.NUMERIC)
    private Double notionalMw, notionalMwh;

    @ManyToOne
    @JoinColumn(name = "parent_profile")
    private CommodityDeliveryProfile parentProfile;

    @OneToMany(mappedBy = "parentProfile")
    private List<CommodityDeliveryProfile> cascadeChildren;

    /**
     * @return the idMasterData
     */
    public Integer getIdMasterData() {
        return idMasterData;
    }

    /**
     * @param idMasterData the idMasterData to set
     */
    public void setIdMasterData(Integer idMasterData) {
        this.idMasterData = idMasterData;
    }

    /**
     * @return the deliveryPeriodType
     */
    public DeliveryPeriodType getDeliveryPeriodType() {
        return deliveryPeriodType;
    }

    /**
     * @param deliveryPeriodType the deliveryPeriodType to set
     */
    public void setDeliveryPeriodType(DeliveryPeriodType deliveryPeriodType) {
        this.deliveryPeriodType = deliveryPeriodType;
    }

    /**
     * @return the deliveryStart
     */
    public java.sql.Date getDeliveryStart() {
        return deliveryStart;
    }

    /**
     * @param deliveryStart the deliveryStart to set
     */
    public void setDeliveryStart(java.sql.Date deliveryStart) {
        this.deliveryStart = deliveryStart;
    }

    /**
     * @return the deliveryEnd
     */
    public java.sql.Date getDeliveryEnd() {
        return deliveryEnd;
    }

    /**
     * @param deliveryEnd the deliveryEnd to set
     */
    public void setDeliveryEnd(java.sql.Date deliveryEnd) {
        this.deliveryEnd = deliveryEnd;
    }

    /**
     * @return the totalDeliveryHours
     */
    public Integer getTotalDeliveryHours() {
        return totalDeliveryHours;
    }

    /**
     * @param totalDeliveryHours the totalDeliveryHours to set
     */
    public void setTotalDeliveryHours(Integer totalDeliveryHours) {
        this.totalDeliveryHours = totalDeliveryHours;
    }

    /**
     * @return the market
     */
    public String getMarket() {
        return market;
    }

    /**
     * @param market the market to set
     */
    public void setMarket(String market) {
        this.market = market;
    }

    /**
     * @return the notionalMw
     */
    public Double getNotionalMw() {
        return notionalMw;
    }

    /**
     * @param notionalMw the notionalMw to set
     */
    public void setNotionalMw(Double notionalMw) {
        this.notionalMw = notionalMw;
    }

    /**
     * @return the notionalMwh
     */
    public Double getNotionalMwh() {
        return notionalMwh;
    }

    /**
     * @param notionalMwh the notionalMwh to set
     */
    public void setNotionalMwh(Double notionalMwh) {
        this.notionalMwh = notionalMwh;
    }

    /**
     * @return the parentProfile
     */
    public CommodityDeliveryProfile getParentProfile() {
        return parentProfile;
    }

    /**
     * @param parentProfile the parentProfile to set
     */
    public void setParentProfile(CommodityDeliveryProfile parentProfile) {
        this.parentProfile = parentProfile;
    }

    /**
     * @return the cascadeChildren
     */
    public List<CommodityDeliveryProfile> getCascadeChildren() {
        return cascadeChildren;
    }

    /**
     * @param cascadeChildren the cascadeChildren to set
     */
    public void setCascadeChildren(List<CommodityDeliveryProfile> cascadeChildren) {
        this.cascadeChildren = cascadeChildren;
    }

    /**
     * @return the masterData
     */
    protected PowerFutureMasterData getMasterData() {
        return masterData;
    }

    /**
     * @param masterData the masterData to set
     */
    protected void setMasterData(PowerFutureMasterData masterData) {
        this.masterData = masterData;
    }

}
