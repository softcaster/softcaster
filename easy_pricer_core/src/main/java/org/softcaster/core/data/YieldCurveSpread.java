package org.softcaster.core.data;

import jakarta.persistence.*;        // use javax.persistence.* if your project is still on Java EE
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.*;        // use javax.persistence.* if your project is still on Java EE
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;

/**
 * One calibrated z-spread bucket of a spread curve (e.g. ITA_SPREADED at 5 years on a given date).
 * Getters and setters omitted: generate them as for your other entities.
 */
@Entity
@Table(name = "yield_curve_spread")
public class YieldCurveSpread implements Serializable {

    @Id
    @SequenceGenerator(name = "yield_curve_spread_seq", sequenceName = "yield_curve_spread_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "yield_curve_spread_seq")
    @Column(name = "id_yield_curve_spread", columnDefinition = "INTEGER")
    private Integer idYieldCurveSpread;

    // Id of the spread curve (table spread_curve)
    @Column(name = "spread_curve", nullable = false)
    private Integer spreadCurveId;

    @Column(name = "as_of_date", nullable = false)
    private LocalDate asOfDate;

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

    /**
     * @return the idYieldCurveSpread
     */
    public Integer getIdYieldCurveSpread() {
        return idYieldCurveSpread;
    }

    /**
     * @param idYieldCurveSpread the idYieldCurveSpread to set
     */
    public void setIdYieldCurveSpread(Integer idYieldCurveSpread) {
        this.idYieldCurveSpread = idYieldCurveSpread;
    }

    /**
     * @return the spreadCurveId
     */
    public Integer getSpreadCurveId() {
        return spreadCurveId;
    }

    /**
     * @param spreadCurveId the spreadCurveId to set
     */
    public void setSpreadCurveId(Integer spreadCurveId) {
        this.spreadCurveId = spreadCurveId;
    }

    /**
     * @return the asOfDate
     */
    public LocalDate getAsOfDate() {
        return asOfDate;
    }

    /**
     * @param asOfDate the asOfDate to set
     */
    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }

    /**
     * @return the baseCurveCode
     */
    public String getBaseCurveCode() {
        return baseCurveCode;
    }

    /**
     * @param baseCurveCode the baseCurveCode to set
     */
    public void setBaseCurveCode(String baseCurveCode) {
        this.baseCurveCode = baseCurveCode;
    }

    /**
     * @return the offsetType
     */
    public Short getOffsetType() {
        return offsetType;
    }

    /**
     * @param offsetType the offsetType to set
     */
    public void setOffsetType(Short offsetType) {
        this.offsetType = offsetType;
    }

    /**
     * @return the offsetValue
     */
    public Short getOffsetValue() {
        return offsetValue;
    }

    /**
     * @param offsetValue the offsetValue to set
     */
    public void setOffsetValue(Short offsetValue) {
        this.offsetValue = offsetValue;
    }

    /**
     * @return the zSpread
     */
    public Double getzSpread() {
        return zSpread;
    }

    /**
     * @param zSpread the zSpread to set
     */
    public void setzSpread(Double zSpread) {
        this.zSpread = zSpread;
    }

    /**
     * @return the nBonds
     */
    public Integer getnBonds() {
        return nBonds;
    }

    /**
     * @param nBonds the nBonds to set
     */
    public void setnBonds(Integer nBonds) {
        this.nBonds = nBonds;
    }

    /**
     * @return the dispersion
     */
    public Double getDispersion() {
        return dispersion;
    }

    /**
     * @param dispersion the dispersion to set
     */
    public void setDispersion(Double dispersion) {
        this.dispersion = dispersion;
    }

    /**
     * @return the calibratedAt
     */
    public LocalDateTime getCalibratedAt() {
        return calibratedAt;
    }

    /**
     * @param calibratedAt the calibratedAt to set
     */
    public void setCalibratedAt(LocalDateTime calibratedAt) {
        this.calibratedAt = calibratedAt;
    }
}

/*
 * Repository queries (YieldCurveSpreadRepository):
 *
 *   @Query("select s from YieldCurveSpread s where s.spreadCurveId = :id "
 *        + "and s.asOfDate = (select max(x.asOfDate) from YieldCurveSpread x "
 *        + "where x.spreadCurveId = :id and x.asOfDate <= :date)")
 *   List<YieldCurveSpread> findLatest(@Param("id") Integer id, @Param("date") LocalDate date);
 *
 *   @Modifying(clearAutomatically = true, flushAutomatically = true)
 *   @Query("delete from YieldCurveSpread s where s.spreadCurveId = :id and s.asOfDate = :date")
 *   void deleteByCurveAndDate(@Param("id") Integer id, @Param("date") LocalDate date);
 */