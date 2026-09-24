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
@Table(name = "market_quote")
@SuppressWarnings("PersistenceUnitPresent")
public class MarketQuote implements Serializable {

    @Id
    @SequenceGenerator(name = "market_quote_seq", sequenceName = "market_quote_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "market_quote_seq")
    @Column(name = "id_market_quote")
    private Integer idMarketQuote;

    @Column(name = "business_date", nullable = false)
    private java.sql.Date businessDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_master_data", nullable = false)
    private CmdFutureMasterData masterData;

    @Convert(converter = LoadTypeConverter.class)
    @Column(name = "load_type", nullable = false)
    private LoadType loadType;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "source", length = 32, nullable = false)
    private String source; // 'MANUAL_ENTRY', 'EEX_FEED', 'VENDOR_X'...

    protected MarketQuote() {
        // richiesto da JPA
    }

    public MarketQuote(java.sql.Date businessDate, CmdFutureMasterData masterData,
            LoadType loadType, Double price, String source) {
        this.businessDate = businessDate;
        this.masterData = masterData;
        this.loadType = loadType;
        this.price = price;
        this.source = source;
    }

    public Integer getIdMarketQuote() {
        return idMarketQuote;
    }

    public java.sql.Date getBusinessDate() {
        return businessDate;
    }

    public CmdFutureMasterData getMasterData() {
        return masterData;
    }

    public LoadType getLoadType() {
        return loadType;
    }

    public Double getPrice() {
        return price;
    }

    public String getSource() {
        return source;
    }
}
