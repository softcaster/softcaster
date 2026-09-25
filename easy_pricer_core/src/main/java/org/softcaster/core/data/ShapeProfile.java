/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.LoadType;

@Entity
@Table(name = "shape_profile")
@SuppressWarnings("PersistenceUnitPresent")
public class ShapeProfile implements Serializable {

    @Id
    @SequenceGenerator(name = "shape_profile_seq", sequenceName = "shape_profile_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shape_profile_seq")
    @Column(name = "shape_profile_id")
    private Integer shapeProfileId;

    @Column(name = "profile_code", length = 32, nullable = false)
    private String profileCode;

    @Column(name = "market", length = 16, nullable = false)
    private String market;

    @Convert(converter = LoadTypeConverter.class)
    @Column(name = "load_type", nullable = false)
    private LoadType loadType;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;   // 1-7, null se il fattore e' solo mensile

    @Column(name = "month_of_year")
    private Integer monthOfYear; // 1-12, null se il fattore e' solo settimanale

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "weight_factor", nullable = false)
    private Double weightFactor;

    @Column(name = "valid_from", nullable = false)
    private java.sql.Date validFrom;

    @Column(name = "valid_to")
    private java.sql.Date validTo; // null = tuttora valido

    protected ShapeProfile() {
        // richiesto da JPA
    }

    public ShapeProfile(String profileCode, String market, LoadType loadType,
            Integer dayOfWeek, Integer monthOfYear, Double weightFactor,
            java.sql.Date validFrom, java.sql.Date validTo) {
        this.profileCode = profileCode;
        this.market = market;
        this.loadType = loadType;
        this.dayOfWeek = dayOfWeek;
        this.monthOfYear = monthOfYear;
        this.weightFactor = weightFactor;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Integer getShapeProfileId() {
        return shapeProfileId;
    }

    public String getProfileCode() {
        return profileCode;
    }

    public String getMarket() {
        return market;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public Integer getMonthOfYear() {
        return monthOfYear;
    }

    public Double getWeightFactor() {
        return weightFactor;
    }

    public java.sql.Date getValidFrom() {
        return validFrom;
    }

    public java.sql.Date getValidTo() {
        return validTo;
    }
}
