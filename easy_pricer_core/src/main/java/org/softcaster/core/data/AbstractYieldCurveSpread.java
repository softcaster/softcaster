package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * Bucket calibrato di z-spread di una spread curve (es. ITA_SPREADED a 5
 * anni). Colonne comuni alla calibrazione corrente (YieldCurveSpread, senza
 * data: la data e' in testata, spread_curve.as_of_date) e allo storico
 * (YieldCurveSpreadHist).
 */
@MappedSuperclass
public abstract class AbstractYieldCurveSpread implements Serializable {

    // Id of the spread curve (table spread_curve)
    @Column(name = "spread_curve", nullable = false)
    private Integer spreadCurveId;

    // Base curve the spreads were CALIBRATED on: compared at load time with the configured base curve
    @Column(name = "base_curve_code", nullable = false, length = 50)
    private String baseCurveCode;

    @Column(name = "offset_type", nullable = false)
    private Short offsetType;

    @Column(name = "offset_value", nullable = false)
    private Short offsetValue;

    // Continuous z-spread, ACT/365, decimal (0.0095 = 95 bp)
    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "z_spread", nullable = false)
    private Double zSpread;

    @Column(name = "n_bonds")
    private Integer nBonds;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "dispersion")
    private Double dispersion;

    @Column(name = "calibrated_at", nullable = false)
    private LocalDateTime calibratedAt = LocalDateTime.now();

    protected AbstractYieldCurveSpread() {
    }

    /**
     * Stessi valori di un altro bucket (corrente o storico).
     */
    protected AbstractYieldCurveSpread(AbstractYieldCurveSpread s) {
        this.spreadCurveId = s.spreadCurveId;
        this.baseCurveCode = s.baseCurveCode;
        this.offsetType = s.offsetType;
        this.offsetValue = s.offsetValue;
        this.zSpread = s.zSpread;
        this.nBonds = s.nBonds;
        this.dispersion = s.dispersion;
        this.calibratedAt = s.calibratedAt;
    }

    /**
     * Data della calibrazione.
     */
    public abstract LocalDate getAsOfDate();

    public Integer getSpreadCurveId() {
        return spreadCurveId;
    }

    public void setSpreadCurveId(Integer spreadCurveId) {
        this.spreadCurveId = spreadCurveId;
    }

    public String getBaseCurveCode() {
        return baseCurveCode;
    }

    public void setBaseCurveCode(String baseCurveCode) {
        this.baseCurveCode = baseCurveCode;
    }

    public Short getOffsetType() {
        return offsetType;
    }

    public void setOffsetType(Short offsetType) {
        this.offsetType = offsetType;
    }

    public Short getOffsetValue() {
        return offsetValue;
    }

    public void setOffsetValue(Short offsetValue) {
        this.offsetValue = offsetValue;
    }

    public Double getzSpread() {
        return zSpread;
    }

    public void setzSpread(Double zSpread) {
        this.zSpread = zSpread;
    }

    public Integer getnBonds() {
        return nBonds;
    }

    public void setnBonds(Integer nBonds) {
        this.nBonds = nBonds;
    }

    public Double getDispersion() {
        return dispersion;
    }

    public void setDispersion(Double dispersion) {
        this.dispersion = dispersion;
    }

    public LocalDateTime getCalibratedAt() {
        return calibratedAt;
    }

    public void setCalibratedAt(LocalDateTime calibratedAt) {
        this.calibratedAt = calibratedAt;
    }
}
