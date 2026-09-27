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
        name = "market_quote",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_market_quote",
                    columnNames = {
                        "market_quote_definition_id",
                        "business_date"
                    }
            )
        }
)
@SuppressWarnings("PersistenceUnitPresent")
public class MarketQuote implements Serializable {

    @Id
    @SequenceGenerator(name = "market_quote_seq", sequenceName = "market_quote_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "market_quote_seq")
    @Column(name = "market_quote_id")
    private Integer marketQuoteId;

    @Column(name = "market_quote_definition_id", insertable = false, updatable = false)
    private Integer marketQuoteDefinition;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(
            name = "price",
            precision = 15,
            scale = 5,
            nullable = false
    )
    private BigDecimal price;

    // getter/setter

    /**
     * @return the marketQuoteId
     */
    public Integer getMarketQuoteId() {
        return marketQuoteId;
    }

    /**
     * @param marketQuoteId the marketQuoteId to set
     */
    public void setMarketQuoteId(Integer marketQuoteId) {
        this.marketQuoteId = marketQuoteId;
    }

    /**
     * @return the marketQuoteDefinition
     */
    public Integer getMarketQuoteDefinition() {
        return marketQuoteDefinition;
    }

    /**
     * @param marketQuoteDefinition the marketQuoteDefinition to set
     */
    public void setMarketQuoteDefinition(Integer marketQuoteDefinition) {
        this.marketQuoteDefinition = marketQuoteDefinition;
    }

    /**
     * @return the businessDate
     */
    public LocalDate getBusinessDate() {
        return businessDate;
    }

    /**
     * @param businessDate the businessDate to set
     */
    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
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
