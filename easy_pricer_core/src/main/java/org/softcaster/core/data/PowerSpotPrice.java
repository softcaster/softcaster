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
    name = "power_spot_price",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_power_spot_price",
            columnNames = {
                "spot_price_definition_id",
                "delivery_date"
            }
        )
    }
)
public class PowerSpotPrice implements Serializable {

    @Id
    @SequenceGenerator(name = "spot_price_seq", sequenceName = "power_spot_price_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_price_seq")
    @Column(name = "spot_price_id")
    private Integer spotPriceId;

    @Column(name = "spot_price_definition_id", insertable = false, updatable = false)
    private Integer spotPriceDefinition;

    @Column(name = "delivery_date", nullable = false)
    private LocalDate deliveryDate;

    @Column(
        name = "price",
        precision = 15,
        scale = 5,
        nullable = false
    )
    private BigDecimal price;

    // getter/setter

    /**
     * @return the spotPriceId
     */
    public Integer getSpotPriceId() {
        return spotPriceId;
    }

    /**
     * @param spotPriceId the spotPriceId to set
     */
    public void setSpotPriceId(Integer spotPriceId) {
        this.spotPriceId = spotPriceId;
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
     * @return the price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}