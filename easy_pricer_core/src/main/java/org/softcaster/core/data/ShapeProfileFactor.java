/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(
    name = "shape_profile_factor"
)
public class ShapeProfileFactor implements Serializable {

    @Id
    @SequenceGenerator(
        name = "shape_profile_factor_seq",
        sequenceName = "shape_profile_factor_s",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "shape_profile_factor_seq"
    )
    @Column(name = "shape_profile_factor_id")
    private Long shapeProfileFactorId;

    @Column(
        name = "shape_profile_id",
        insertable = false,
        updatable = false
    )
    private Integer shapeProfile;

    @Column(name = "month_of_year")
    private Short monthOfYear;

    @Column(name = "day_of_week")
    private Short dayOfWeek;

    @Column(name = "hour_of_day")
    private Short hourOfDay;

    @Column(name = "is_holiday")
    private Boolean holiday;

    @Column(name = "weight_factor")
    private BigDecimal weightFactor;

    // getter / setter

    /**
     * @return the shapeProfileFactorId
     */
    public Long getShapeProfileFactorId() {
        return shapeProfileFactorId;
    }

    /**
     * @param shapeProfileFactorId the shapeProfileFactorId to set
     */
    public void setShapeProfileFactorId(Long shapeProfileFactorId) {
        this.shapeProfileFactorId = shapeProfileFactorId;
    }

    /**
     * @return the shapeProfile
     */
    public Integer getShapeProfile() {
        return shapeProfile;
    }

    /**
     * @param shapeProfile the shapeProfile to set
     */
    public void setShapeProfile(Integer shapeProfile) {
        this.shapeProfile = shapeProfile;
    }

    /**
     * @return the monthOfYear
     */
    public Short getMonthOfYear() {
        return monthOfYear;
    }

    /**
     * @param monthOfYear the monthOfYear to set
     */
    public void setMonthOfYear(Short monthOfYear) {
        this.monthOfYear = monthOfYear;
    }

    /**
     * @return the dayOfWeek
     */
    public Short getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(Short dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the hourOfDay
     */
    public Short getHourOfDay() {
        return hourOfDay;
    }

    /**
     * @param hourOfDay the hourOfDay to set
     */
    public void setHourOfDay(Short hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    /**
     * @return the holiday
     */
    public Boolean getHoliday() {
        return holiday;
    }

    /**
     * @param holiday the holiday to set
     */
    public void setHoliday(Boolean holiday) {
        this.holiday = holiday;
    }

    /**
     * @return the weightFactor
     */
    public BigDecimal getWeightFactor() {
        return weightFactor;
    }

    /**
     * @param weightFactor the weightFactor to set
     */
    public void setWeightFactor(BigDecimal weightFactor) {
        this.weightFactor = weightFactor;
    }
}