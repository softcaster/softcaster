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
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(
        name = "shape_profile_spot_series",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_shape_profile_spot_series",
                    columnNames = {
                        "shape_profile_id",
                        "spot_price_definition_id"
                    }
            )
        }
)
public class ShapeProfileSpotSeries implements Serializable {

    @Id
    @SequenceGenerator(
            name = "shape_profile_spot_series_seq",
            sequenceName = "shape_profile_spot_series_s",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "shape_profile_spot_series_seq"
    )
    @Column(name = "shape_profile_spot_series_id")
    private Long shapeProfileSpotSeriesId;

    @Column(
            name = "shape_profile_id",
            insertable = false,
            updatable = false
    )
    private Integer shapeProfile;

    @Column(
            name = "spot_price_definition_id",
            insertable = false,
            updatable = false
    )
    private Integer spotPriceDefinition;

    @Column(name = "observation_from")
    private LocalDate observationFrom;

    @Column(name = "observation_to")
    private LocalDate observationTo;

    /**
     * @return the shapeProfileSpotSeriesId
     */
    public Long getShapeProfileSpotSeriesId() {
        return shapeProfileSpotSeriesId;
    }

    /**
     * @param shapeProfileSpotSeriesId the shapeProfileSpotSeriesId to set
     */
    public void setShapeProfileSpotSeriesId(Long shapeProfileSpotSeriesId) {
        this.shapeProfileSpotSeriesId = shapeProfileSpotSeriesId;
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
     * @return the spotPriceDefinition
     */
    public Integer getSpotPriceDefinition() {
        return spotPriceDefinition;
    }

    /**
     * @param spotPriceDefinition the spotPriceDefinition to set
     */
    public void setSpotPriceDefinition(Integer spotPriceDefinition) {
        this.spotPriceDefinition = spotPriceDefinition;
    }

    /**
     * @return the observationFrom
     */
    public LocalDate getObservationFrom() {
        return observationFrom;
    }

    /**
     * @param observationFrom the observationFrom to set
     */
    public void setObservationFrom(LocalDate observationFrom) {
        this.observationFrom = observationFrom;
    }

    /**
     * @return the observationTo
     */
    public LocalDate getObservationTo() {
        return observationTo;
    }

    /**
     * @param observationTo the observationTo to set
     */
    public void setObservationTo(LocalDate observationTo) {
        this.observationTo = observationTo;
    }
}
