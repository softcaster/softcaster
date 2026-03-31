package org.softcaster.easy_pricer_core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "currency_pair")
@SuppressWarnings("PersistenceUnitPresent")

public class CurrencyPair implements Serializable {

    @Id
    @SequenceGenerator(name = "currency_pair_seq", sequenceName = "currency_pair_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "currency_pair_seq")
    @Column(name = "id_currency_pair", columnDefinition = "INTEGER")
    private Integer idCurrencyPair;

    @Column(name = "code")
    private String code;

    @Column(name = "bcy")
    private Integer bcy;

    @Column(name = "ccy")
    private Integer ccy;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "bid")
    private Double bid;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ask")
    private Double ask;

    public Integer getIdCurrencyPair() {
        return idCurrencyPair;
    }

    public void setIdCurrencyPair(Integer idCurrencyPair) {
        this.idCurrencyPair = idCurrencyPair;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getBcy() {
        return bcy;
    }

    public void setBcy(Integer bcy) {
        this.bcy = bcy;
    }

    public Integer getCcy() {
        return ccy;
    }

    public void setCcy(Integer ccy) {
        this.ccy = ccy;
    }

    public Double getBid() {
        return bid;
    }

    public void setBid(Double bid) {
        this.bid = bid;
    }

    public Double getAsk() {
        return ask;
    }

    public void setAsk(Double ask) {
        this.ask = ask;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdCurrencyPair() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CurrencyPair that = (CurrencyPair) obj;
        return getIdCurrencyPair().equals(that.getIdCurrencyPair());
    }

    @Override
    public int hashCode() {
        return getIdCurrencyPair() == null ? 0 : idCurrencyPair.hashCode();
    }
}
