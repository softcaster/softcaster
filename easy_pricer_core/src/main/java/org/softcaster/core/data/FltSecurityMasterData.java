package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.CouponProjectionMethodConverter;
import org.softcaster.core.data.converters.FixingDayTypeConverter;
import org.softcaster.engine.enums.CouponProjectionMethod;
import org.softcaster.engine.enums.FixingDayType;

@Entity
@Table(name = "flt_security_master_data")
@SuppressWarnings("PersistenceUnitPresent")

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "FltSecurityMasterData.fullGraph",
            attributeNodes = {
                @NamedAttributeNode("currency"),
                @NamedAttributeNode("assetClass"),
                @NamedAttributeNode("instrumentValuation"),
                @NamedAttributeNode("issuer"),
            }),
    @NamedEntityGraph(
            name = "FltSecurityMasterData.indexGraph",
            attributeNodes = {
                @NamedAttributeNode("refRateIndex")
            })
})
public class FltSecurityMasterData extends SecurityMasterData {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "spread")
    private Double spread;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "floor")
    private Double floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_index", nullable = true)
    private RefRateIndex projectionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_index", nullable = true)
    private YieldCurveEntity yieldCurve;

    @Convert(converter = CouponProjectionMethodConverter.class)
    @Column(name = "coupon_pm")
    private CouponProjectionMethod couponPm;

    @Column(name = "fixing_lag_days")
    private Integer fixingLagDays;

    @Column(name = "index_months")
    private Integer indexMonths;
    
    @Convert(converter = FixingDayTypeConverter.class)
    @Column(name = "fixing_day_type")
    private FixingDayType fixingDayType;
    
    public Double getSpread() {
        return spread;
    }

    public void setSpread(Double spread) {
        this.spread = spread;
    }

    /**
     * @return the couponPm
     */
    public CouponProjectionMethod getCouponPm() {
        return couponPm;
    }

    /**
     * @param couponPm the couponPm to set
     */
    public void setCouponPm(CouponProjectionMethod couponPm) {
        this.couponPm = couponPm;
    }

    /**
     * @return the fixingDayType
     */
    public FixingDayType getFixingDayType() {
        return fixingDayType;
    }

    /**
     * @param fixingDayType the fixingDayType to set
     */
    public void setFixingDayType(FixingDayType fixingDayType) {
        this.fixingDayType = fixingDayType;
    }

    /**
     * @return the floor
     */
    public Double getFloor() {
        return floor;
    }

    /**
     * @param floor the floor to set
     */
    public void setFloor(Double floor) {
        this.floor = floor;
    }

    /**
     * @return the projectionIndex
     */
    public RefRateIndex getProjectionIndex() {
        return projectionIndex;
    }

    /**
     * @param projectionIndex the projectionIndex to set
     */
    public void setProjectionIndex(RefRateIndex projectionIndex) {
        this.projectionIndex = projectionIndex;
    }

    /**
     * @return the yieldCurve
     */
    public YieldCurveEntity getYieldCurve() {
        return yieldCurve;
    }

    /**
     * @param yieldCurve the yieldCurve to set
     */
    public void setYieldCurve(YieldCurveEntity yieldCurve) {
        this.yieldCurve = yieldCurve;
    }

    /**
     * @return the fixingLagDays
     */
    public Integer getFixingLagDays() {
        return fixingLagDays;
    }

    /**
     * @param fixingLagDays the fixingLagDays to set
     */
    public void setFixingLagDays(Integer fixingLagDays) {
        this.fixingLagDays = fixingLagDays;
    }

    /**
     * @return the indexMonths
     */
    public Integer getIndexMonths() {
        return indexMonths;
    }

    /**
     * @param indexMonths the indexMonths to set
     */
    public void setIndexMonths(Integer indexMonths) {
        this.indexMonths = indexMonths;
    }

    
}
