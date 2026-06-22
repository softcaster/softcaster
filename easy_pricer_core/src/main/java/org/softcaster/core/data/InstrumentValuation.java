package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
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

    // --- RELAZIONE BIDIREZIONALE (LATO PROPRIETARIO) ---
    // nullable = false indica che InstrumentValuation non puo'esistere al di fuori
    // di un oggetto MasterData
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_data", referencedColumnName = "id_master_data", nullable = false)
    private MasterData masterData;

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

    @Column(name = "valuation_date")
    private LocalDate valuationDate;

    public Integer getInstrumentValuationId() {
        return instrumentValuationId;
    }

    public void setInstrumentValuationId(Integer instrumentValuationId) {
        this.instrumentValuationId = instrumentValuationId;
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

    /**
     * @return the masterData
     */
    public MasterData getMasterData() {
        return masterData;
    }

    /**
     * @param masterData the masterData to set
     */
    public void setMasterData(MasterData masterData) {
        this.masterData = masterData;
    }

    /**
     * @return the valuationDate
     */
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    /**
     * @param valuationDate the valuationDate to set
     */
    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }
}
