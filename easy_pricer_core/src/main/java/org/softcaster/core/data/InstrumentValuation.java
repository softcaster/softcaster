package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Table(name = "instrument_valuation")
@SuppressWarnings("PersistenceUnitPresent")

public class InstrumentValuation implements Serializable {

    @Id
    @SequenceGenerator(name = "instrument_valuation_seq", sequenceName = "instrument_valuation_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "instrument_valuation_seq")
    @Column(name = "instrument_valuation_id")
    private Integer instrumentValuationId;

    @Column(name = "master_data", insertable = false, updatable = false)
    private Integer masterData;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "market_price")
    private Double marketPrice;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "accrued_interest")
    private Double accruedInterest;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "ytm")
    private Double ytm;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "duration")
    private Double duration;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "mod_duration")
    private Double modDuration;

    @JdbcTypeCode(Types.NUMERIC)
    @Column(name = "theoretical_price")
    private Double theoreticalPrice;

    public Integer getInstrumentValuationId() {
        return instrumentValuationId;
    }

    public void setInstrumentValuationId(Integer instrumentValuationId) {
        this.instrumentValuationId = instrumentValuationId;
    }

    public Integer getMasterData() {
        return masterData;
    }

    public void setMasterData(Integer masterData) {
        this.masterData = masterData;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getAccruedInterest() {
        return accruedInterest;
    }

    public void setAccruedInterest(Double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public Double getYtm() {
        return ytm;
    }

    public void setYtm(Double ytm) {
        this.ytm = ytm;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getModDuration() {
        return modDuration;
    }

    public void setModDuration(Double modDuration) {
        this.modDuration = modDuration;
    }

    public Double getTheoreticalPrice() {
        return theoreticalPrice;
    }

    public void setTheoreticalPrice(Double theoreticalPrice) {
        this.theoreticalPrice = theoreticalPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (instrumentValuationId == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        InstrumentValuation that = (InstrumentValuation) obj;
        return instrumentValuationId.equals(that.instrumentValuationId);
    }

    @Override
    public int hashCode() {
        return instrumentValuationId == null ? 0 : instrumentValuationId.hashCode();
    }
}
