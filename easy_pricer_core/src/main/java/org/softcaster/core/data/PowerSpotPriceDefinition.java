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
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.LoadTypeConverter;
import org.softcaster.core.data.converters.MarketDataSourceConverter;
import org.softcaster.engine.enums.LoadType;
import org.softcaster.engine.enums.MarketDataSource;

@Entity
@Table(
        name = "power_spot_price_definition",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_power_spot_price_definition",
                    columnNames = {
                        "id_country",
                        "load_type",
                        "data_source"
                    }
            )
        }
)
public class PowerSpotPriceDefinition implements Serializable {

    @Id
    @SequenceGenerator(name = "spot_price_definition_seq", sequenceName = "power_spot_price_definition_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spot_price_definition_seq")
    @Column(name = "spot_price_definition_id")
    private Integer idSpotPriceDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_country",
            nullable = false
    )
    private Country country;

    @Convert(converter = LoadTypeConverter.class)
    private LoadType loadType;

    @Convert(converter = MarketDataSourceConverter.class)
    private MarketDataSource dataSource;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "spot_price_definition_id", nullable = false) // FK in child table power_spot_price
    private List<PowerSpotPrice> prices = new ArrayList<>();

    // getter/setter

    /**
     * @return the idSpotPriceDefinition
     */
    public Integer getIdSpotPriceDefinition() {
        return idSpotPriceDefinition;
    }

    /**
     * @param idSpotPriceDefinition the idSpotPriceDefinition to set
     */
    public void setIdSpotPriceDefinition(Integer idSpotPriceDefinition) {
        this.idSpotPriceDefinition = idSpotPriceDefinition;
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
     * @return the prices
     */
    public List<PowerSpotPrice> getPrices() {
        return prices;
    }

    /**
     * @param prices the prices to set
     */
    public void setPrices(List<PowerSpotPrice> prices) {
        this.prices = prices;
    }
}
