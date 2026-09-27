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
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
    name = "granular_curve_point",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_granular_curve_point",
            columnNames = {
                "granular_curve_id",
                "delivery_date"
            }
        )
    }
)
public class GranularCurvePoint implements Serializable {

    @Id
    @SequenceGenerator(
        name = "granular_curve_point_seq",
        sequenceName = "granular_curve_point_s",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "granular_curve_point_seq"
    )
    @Column(name = "granular_curve_point_id")
    private Long granularCurvePointId;

    @Column(
        name = "granular_curve_id",
        insertable = false,
        updatable = false
    )
    private Integer granularCurve;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(
        name = "forward_price",
        precision = 15,
        scale = 5,
        nullable = false
    )
    private BigDecimal forwardPrice;

    // getter / setter

    /**
     * @return the granularCurvePointId
     */
    public Long getGranularCurvePointId() {
        return granularCurvePointId;
    }

    /**
     * @param granularCurvePointId the granularCurvePointId to set
     */
    public void setGranularCurvePointId(Long granularCurvePointId) {
        this.granularCurvePointId = granularCurvePointId;
    }

    /**
     * @return the granularCurve
     */
    public Integer getGranularCurve() {
        return granularCurve;
    }

    /**
     * @param granularCurve the granularCurve to set
     */
    public void setGranularCurve(Integer granularCurve) {
        this.granularCurve = granularCurve;
    }

    /**
     * @return the deliveryDate
     */
    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * @param deliveryDate the deliveryDate to set
     */
    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    /**
     * @return the forwardPrice
     */
    public BigDecimal getForwardPrice() {
        return forwardPrice;
    }

    /**
     * @param forwardPrice the forwardPrice to set
     */
    public void setForwardPrice(BigDecimal forwardPrice) {
        this.forwardPrice = forwardPrice;
    }
}