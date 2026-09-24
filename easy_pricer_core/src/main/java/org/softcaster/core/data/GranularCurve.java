/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.engine.enums.LoadType;

@Entity
@Table(name = "granular_curve")
@SuppressWarnings("PersistenceUnitPresent")
public class GranularCurve implements Serializable {

    @Id
    @SequenceGenerator(name = "granular_curve_seq", sequenceName = "granular_curve_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "granular_curve_seq")
    @Column(name = "id_granular_curve")
    private Long idGranularCurve;

    @Column(name = "business_date", nullable = false)
    private java.sql.Date businessDate;

    @Column(name = "delivery_date", nullable = false)
    private java.sql.Date deliveryDate;

    @Convert(converter = LoadTypeConverter.class)
    @Column(name = "load_type", nullable = false)
    private LoadType loadType;

    @Column(name = "market", length = 16, nullable = false)
    private String market;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "price", nullable = false)
    private Double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_market_quote", nullable = false)
    private MarketQuote sourceMarketQuote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_shape_profile", nullable = false)
    private ShapeProfile sourceShapeProfile;

    protected GranularCurve() {
        // richiesto da JPA
    }

    public GranularCurve(java.sql.Date businessDate, java.sql.Date deliveryDate,
            LoadType loadType, String market, Double price,
            MarketQuote sourceMarketQuote, ShapeProfile sourceShapeProfile) {
        this.businessDate = businessDate;
        this.deliveryDate = deliveryDate;
        this.loadType = loadType;
        this.market = market;
        this.price = price;
        this.sourceMarketQuote = sourceMarketQuote;
        this.sourceShapeProfile = sourceShapeProfile;
    }

    public Long getIdGranularCurve() {
        return idGranularCurve;
    }

    public java.sql.Date getBusinessDate() {
        return businessDate;
    }

    public java.sql.Date getDeliveryDate() {
        return deliveryDate;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public String getMarket() {
        return market;
    }

    public Double getPrice() {
        return price;
    }

    public MarketQuote getSourceMarketQuote() {
        return sourceMarketQuote;
    }

    public ShapeProfile getSourceShapeProfile() {
        return sourceShapeProfile;
    }
}
