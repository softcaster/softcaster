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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_rate_index", nullable = true)
    private RefRateIndex refRateIndex;

    @Convert(converter = CouponProjectionMethodConverter.class)
    @Column(name = "coupon_pm")
    private CouponProjectionMethod couponPm;

    @Column(name = "fixing_days_before")
    private Integer fixingDaysBefore;
    
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
     * @return the refRateIndex
     */
    public RefRateIndex getRefRateIndex() {
        return refRateIndex;
    }

    /**
     * @param refRateIndex the refRateIndex to set
     */
    public void setRefRateIndex(RefRateIndex refRateIndex) {
        this.refRateIndex = refRateIndex;
    }

    /**
     * @return the fixingDaysBefore
     */
    public Integer getFixingDaysBefore() {
        return fixingDaysBefore;
    }

    /**
     * @param fixingDaysBefore the fixingDaysBefore to set
     */
    public void setFixingDaysBefore(Integer fixingDaysBefore) {
        this.fixingDaysBefore = fixingDaysBefore;
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
}
