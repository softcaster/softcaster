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
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.softcaster.core.data.converters.MarketDataSourceConverter;
import org.softcaster.engine.enums.MarketDataSource;

@Entity
@Table(name = "market_quote_definition",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_market_quote_definition",
            columnNames = {"id_master_data", "data_source"}
        )
    }
)
@SuppressWarnings("PersistenceUnitPresent")

@NamedEntityGraphs({
    @NamedEntityGraph(
            name = "MarketQuoteDefinition.fullWithCountry",
            attributeNodes = {
                @NamedAttributeNode("country")}
    ),
    @NamedEntityGraph(
            name = "MarketQuoteDefinition.fullWithQuotes",
            attributeNodes = {
                @NamedAttributeNode("quotes")}
    )
})
public class MarketQuoteDefinition implements Serializable {

    @Id
    @SequenceGenerator(name = "market_quote_definition_seq", sequenceName = "market_quote_definition_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "market_quote_definition_seq")
    @Column(name = "market_quote_definition_id")
    private Integer marketQuoteDefinitionId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "id_country",
        nullable = false
    )
    private Country country;

    @Convert(converter = MarketDataSourceConverter.class)
    private MarketDataSource dataSource;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "market_quote_definition_id", nullable = false) // FK in child table market_quote
    private List<MarketQuote> quotes = new ArrayList<>();

    // getter/setter

    /**
     * @return the marketQuoteDefinitionId
     */
    public Integer getMarketQuoteDefinitionId() {
        return marketQuoteDefinitionId;
    }

    /**
     * @param marketQuoteDefinitionId the marketQuoteDefinitionId to set
     */
    public void setMarketQuoteDefinitionId(Integer marketQuoteDefinitionId) {
        this.marketQuoteDefinitionId = marketQuoteDefinitionId;
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
     * @return the quotes
     */
    public List<MarketQuote> getQuotes() {
        return quotes;
    }

    /**
     * @param quotes the quotes to set
     */
    public void setQuotes(List<MarketQuote> quotes) {
        this.quotes = quotes;
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
}
