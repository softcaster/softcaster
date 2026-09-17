/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.DeliveryPeriodTypeConverter;
import org.softcaster.engine.enums.DeliveryPeriodType;

@Entity
@Table(name = "extended_delivery_future_master_data")
@SuppressWarnings("PersistenceUnitPresent")
public abstract class ExtendedDeliveryFutureMasterData extends CmdFutureMasterData {

    @Convert(converter = DeliveryPeriodTypeConverter.class)
    @Column(name = "delivery_period_type", nullable = false)
    private DeliveryPeriodType deliveryPeriodType;

    @Column(name = "delivery_start", nullable = false)
    private java.sql.Date deliveryStart;

    @Column(name = "delivery_end", nullable = false)
    private java.sql.Date deliveryEnd;

    @Column(name = "total_delivery_hours", nullable = false)
    private Integer totalDeliveryHours;

    @Column(name = "market", length = 16, nullable = false)
    private String market;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "notional_mw", nullable = false)
    private Double notionalMw;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "notional_mwh", nullable = false)
    private Double notionalMwh;

    // self-referencing per il cascading: tipizzato sulla classe astratta,
    // cosi' un Gas Future potra' cascare solo su altri contratti con
    // delivery period esteso, mai su un Crude Oil (che non ha questo livello)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_master_data")
    private ExtendedDeliveryFutureMasterData parentContract;

    @OneToMany(mappedBy = "parentContract", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ExtendedDeliveryFutureMasterData> cascadeChildren = new ArrayList<>();

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
     * @return the parentContract
     */
    public ExtendedDeliveryFutureMasterData getParentContract() {
        return parentContract;
    }

    /**
     * @param parentContract the parentContract to set
     */
    public void setParentContract(ExtendedDeliveryFutureMasterData parentContract) {
        this.parentContract = parentContract;
    }

    /**
     * @return the cascadeChildren
     */
    public List<ExtendedDeliveryFutureMasterData> getCascadeChildren() {
        return cascadeChildren;
    }

    /**
     * @param cascadeChildren the cascadeChildren to set
     */
    public void setCascadeChildren(List<ExtendedDeliveryFutureMasterData> cascadeChildren) {
        this.cascadeChildren = cascadeChildren;
    }
}