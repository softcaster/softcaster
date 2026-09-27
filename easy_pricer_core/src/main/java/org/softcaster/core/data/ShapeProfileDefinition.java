/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.core.data.converters.MarketDataSourceConverter;
import org.softcaster.core.data.converters.ShapeCalculationMethodConverter;
import org.softcaster.core.data.converters.ShapeGranularityConverter;
import org.softcaster.engine.enums.LoadType;
import org.softcaster.engine.enums.MarketDataSource;
import org.softcaster.engine.enums.ShapeCalculationMethod;
import org.softcaster.engine.enums.ShapeGranularity;

@Entity
@Table(
        name = "shape_profile_definition",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_shape_profile_code",
                    columnNames = {"profile_code"}
            )
        }
)
public class ShapeProfileDefinition implements Serializable {

    @Id
    @SequenceGenerator(name = "shape_profile_definition_def", sequenceName = "shape_profile_definition_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shape_profile_definition_def")
    @Column(name = "shape_profile_id")
    private Integer shapeProfileId;

    @Column(
            name = "profile_code",
            length = 64,
            nullable = false
    )
    private String profileCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_country",
            nullable = false
    )
    private Country country;

    @Convert(converter = LoadTypeConverter.class)
    private LoadType loadType;

    @Convert(converter = ShapeCalculationMethodConverter.class)
    private ShapeCalculationMethod calculationMethod;

    @Convert(converter = ShapeGranularityConverter.class)
    private ShapeGranularity granularity;

    @Convert(converter = MarketDataSourceConverter.class)
    private MarketDataSource dataSource;

    @Column(name = "historical_years")
    private Integer historicalYears;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(
        name = "shape_profile_id",
        nullable = false
    )
    private List<ShapeProfileSpotSeries> spotSeries =
        new ArrayList<>();
     
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(
            name = "shape_profile_id",
            nullable = false
    )
    private List<ShapeProfileFactor> factors
            = new ArrayList<>();

    // getter/setter
    /**
     * @return the shapeProfileId
     */
    public Integer getShapeProfileId() {
        return shapeProfileId;
    }

    /**
     * @param shapeProfileId the shapeProfileId to set
     */
    public void setShapeProfileId(Integer shapeProfileId) {
        this.shapeProfileId = shapeProfileId;
    }

    /**
     * @return the profileCode
     */
    public String getProfileCode() {
        return profileCode;
    }

    /**
     * @param profileCode the profileCode to set
     */
    public void setProfileCode(String profileCode) {
        this.profileCode = profileCode;
    }

    /**
     * @return the country
     */
    public Country getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(Country country) {
        this.country = country;
    }

    /**
     * @return the loadType
     */
    public LoadType getLoadType() {
        return loadType;
    }

    /**
     * @param loadType the loadType to set
     */
    public void setLoadType(LoadType loadType) {
        this.loadType = loadType;
    }

    /**
     * @return the calculationMethod
     */
    public ShapeCalculationMethod getCalculationMethod() {
        return calculationMethod;
    }

    /**
     * @param calculationMethod the calculationMethod to set
     */
    public void setCalculationMethod(ShapeCalculationMethod calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    /**
     * @return the granularity
     */
    public ShapeGranularity getGranularity() {
        return granularity;
    }

    /**
     * @param granularity the granularity to set
     */
    public void setGranularity(ShapeGranularity granularity) {
        this.granularity = granularity;
    }

    /**
     * @return the dataSource
     */
    public MarketDataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(MarketDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return the historicalYears
     */
    public Integer getHistoricalYears() {
        return historicalYears;
    }

    /**
     * @param historicalYears the historicalYears to set
     */
    public void setHistoricalYears(Integer historicalYears) {
        this.historicalYears = historicalYears;
    }

    /**
     * @return the validFrom
     */
    public LocalDate getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public LocalDate getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the factors
     */
    public List<ShapeProfileFactor> getFactors() {
        return factors;
    }

    /**
     * @param factors the factors to set
     */
    public void setFactors(List<ShapeProfileFactor> factors) {
        this.factors = factors;
    }

    /**
     * @return the spotSeries
     */
    public List<ShapeProfileSpotSeries> getSpotSeries() {
        return spotSeries;
    }

    /**
     * @param spotSeries the spotSeries to set
     */
    public void setSpotSeries(List<ShapeProfileSpotSeries> spotSeries) {
        this.spotSeries = spotSeries;
    }
}
