package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.CouponProjectionMethodConverter;
import org.softcaster.core.data.converters.FixingDayTypeConverter;
import org.softcaster.engine.enums.CouponProjectionMethod;
import org.softcaster.engine.enums.FixingDayType;

/**
 * Condizioni di un tasso variabile, comuni a titoli (flt_security_master_data)
 * e mutui (flt_loan_master_data): stesse colonne nelle due tabelle.
 *
 * Cedola/rata = indice (projectionIndex, fissato fixingLagDays prima
 * dell'inizio del periodo, durata indexMonths) + spread, limitato da floor e
 * cap. spread, floor e cap in decimale (0.0075 = 75 punti base).
 */
@Embeddable
public class FloatingRateTerms implements Serializable {

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "spread")
    private Double spread;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "floor")
    private Double floor;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "cap")
    private Double cap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projection_index", nullable = true)
    private RefRateIndex projectionIndex;

    @Column(name = "projection_curve")
    private Integer projectionCurveId;

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

    public Double getFloor() {
        return floor;
    }

    public void setFloor(Double floor) {
        this.floor = floor;
    }

    public Double getCap() {
        return cap;
    }

    public void setCap(Double cap) {
        this.cap = cap;
    }

    public RefRateIndex getProjectionIndex() {
        return projectionIndex;
    }

    public void setProjectionIndex(RefRateIndex projectionIndex) {
        this.projectionIndex = projectionIndex;
    }

    public Integer getProjectionCurveId() {
        return projectionCurveId;
    }

    public void setProjectionCurveId(Integer projectionCurveId) {
        this.projectionCurveId = projectionCurveId;
    }

    public CouponProjectionMethod getCouponPm() {
        return couponPm;
    }

    public void setCouponPm(CouponProjectionMethod couponPm) {
        this.couponPm = couponPm;
    }

    public Integer getFixingLagDays() {
        return fixingLagDays;
    }

    public void setFixingLagDays(Integer fixingLagDays) {
        this.fixingLagDays = fixingLagDays;
    }

    public Integer getIndexMonths() {
        return indexMonths;
    }

    public void setIndexMonths(Integer indexMonths) {
        this.indexMonths = indexMonths;
    }

    public FixingDayType getFixingDayType() {
        return fixingDayType;
    }

    public void setFixingDayType(FixingDayType fixingDayType) {
        this.fixingDayType = fixingDayType;
    }
}
