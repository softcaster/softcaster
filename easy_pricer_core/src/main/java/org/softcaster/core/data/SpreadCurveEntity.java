/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.*;        // use javax.persistence.* if your project is still on Java EE
import java.io.Serializable;

/**
 * Definition of a spread curve (e.g. ITA_SPREADED = ECB + calibrated
 * z-spreads). It has no yield curve items: rates come from the base curve,
 * spreads from YieldCurveSpread rows. Getters and setters omitted: generate
 * them as for your other entities.
 */
import jakarta.persistence.*;        // use javax.persistence.* if your project is still on Java EE
import java.io.Serializable;

/**
 * Definition of a spread curve (e.g. ITA_SPREADED = ECB + calibrated z-spreads).
 * It has no yield curve items: rates come from the base curve, spreads from YieldCurveSpread rows.
 * Getters and setters omitted: generate them as for your other entities.
 */
@Entity
@Table(name = "spread_curve")
public class SpreadCurveEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "spread_curve_seq", sequenceName = "spread_curve_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spread_curve_seq")
    @Column(name = "id_spread_curve", columnDefinition = "INTEGER")
    private Integer idSpreadCurve;

    // Must be unique across yield_curve.code too (enforce it in the application: two tables, one namespace)
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", length = 200)
    private String description;

    // Spread-free curve this one is derived from (e.g. ECB). Currency and calendar come from it.
    // LAZY: access it inside a transaction, or load it with "join fetch" (see the repository note below).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "base_curve", nullable = false)
    private YieldCurveEntity baseCurve;

    @Column(name = "max_age_days", nullable = false)
    private Integer maxAgeDays = 5;

    @Column(name = "min_bonds", nullable = false)
    private Integer minBonds = 3;

    @Column(name = "min_valid_buckets", nullable = false)
    private Integer minValidBuckets = 3;

    /**
     * @return the idSpreadCurve
     */
    public Integer getIdSpreadCurve() {
        return idSpreadCurve;
    }

    /**
     * @param idSpreadCurve the idSpreadCurve to set
     */
    public void setIdSpreadCurve(Integer idSpreadCurve) {
        this.idSpreadCurve = idSpreadCurve;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the baseCurve
     */
    public YieldCurveEntity getBaseCurve() {
        return baseCurve;
    }

    /**
     * @param baseCurve the baseCurve to set
     */
    public void setBaseCurve(YieldCurveEntity baseCurve) {
        this.baseCurve = baseCurve;
    }

    /**
     * @return the maxAgeDays
     */
    public Integer getMaxAgeDays() {
        return maxAgeDays;
    }

    /**
     * @param maxAgeDays the maxAgeDays to set
     */
    public void setMaxAgeDays(Integer maxAgeDays) {
        this.maxAgeDays = maxAgeDays;
    }

    /**
     * @return the minBonds
     */
    public Integer getMinBonds() {
        return minBonds;
    }

    /**
     * @param minBonds the minBonds to set
     */
    public void setMinBonds(Integer minBonds) {
        this.minBonds = minBonds;
    }

    /**
     * @return the minValidBuckets
     */
    public Integer getMinValidBuckets() {
        return minValidBuckets;
    }

    /**
     * @param minValidBuckets the minValidBuckets to set
     */
    public void setMinValidBuckets(Integer minValidBuckets) {
        this.minValidBuckets = minValidBuckets;
    }
}

/*
 * Repository note: to read the definition together with its base curve outside a transaction:
 *
 *   @Query("select c from SpreadCurveEntity c join fetch c.baseCurve where c.code = :code")
 *   SpreadCurveEntity findByCodeWithBase(@Param("code") String code);
 */